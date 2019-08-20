/*
Program that compares distribution of languages spoken by zip code from zip_codes table in postgres to
the percentages of bid requests by each zip code that are english, spanish, etc.
*/
package main

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"math"
	"os"
	"strconv"

	_ "github.com/lib/pq"
	gn "gonum.org/v1/gonum/stat"
	plt "gonum.org/v1/plot"
	pltr "gonum.org/v1/plot/plotter"
	vg "gonum.org/v1/plot/vg"
)

//for reading census
type Fields struct {
	zip                string
	only_english       sql.NullString
	spanish            sql.NullString
	other_indoeuropean sql.NullString
	asian_pacific      sql.NullString
	other              sql.NullString
}

//make this a map string index array value for O(1) lookups
type BidFields struct {
	zip         string
	dof         string
	languages   []string
	eng_total   string
	lang2_total string
}

type DBFields struct {
	zip             string
	totalRequests   string
	requestLangs    string
	engTotal        string
	otherTotal      string
	cEngPercent     string
	cSpanishPercent string
	cIndoEuroPerc   string
	cAsianPac       string
	cOther          string
	chiSq           string
	sumStdDev       string
}

func main() {

	//map of # of requests by language by zip code
	rMap := *readJson()

	//map of zip codes and the & of each language spoken in that zip code, from english, spanish, indo_euro, asian, other
	cMap := *readCensus()

	//compare maps and append the matched zip code keys to a slice (6276 matches)
	matches := *findMatches(rMap, cMap)

	//create new map of bid request zip codes mapped to a slice with languages then # of bids in those languages (limit 2 languages)
	nMap := *createMap(matches, rMap)

	//get zip codes with two or more langs to do a chi square distribution with 1 dof (2 params)
	twoLangs := *getTwoLangZips(nMap)

	//calculate chi squares
	chiSquares := *performChiSquare(twoLangs, nMap, cMap)

	//calculate natural logs of chi squares to plot
	cs := *prepHistogram(chiSquares)

	//need to sort in order to calculate median and third quartile
	sortedCS := *SelSort(cs)

	outliersCS := *getOutliersCS(chiSquares, sortedCS)

	plotHistogram(sortedCS)

	//Now, calculate standard deviations and get outliers
	stds := *calcSTD(twoLangs, nMap, cMap)

	stdsSl := *prepSTD(stds)

	sortedSTD := *SelSort(stdsSl)

	outliersSTD := *getOutliersSTD(stds, sortedSTD)

	fmt.Println(len(outliersCS))  //1651
	fmt.Println(len(outliersSTD)) //1576

	//find outliers that are over 75% percentile in Chi-squared and standard deviation
	outliers := *findOutlierMatches(outliersCS, outliersSTD)
	fmt.Println(len(twoLangs)) //6303
	fmt.Println(len(outliers)) //1119

	added := writeToDB(cMap, nMap, outliersCS, outliersSTD, outliers)
	if added {
		fmt.Println("added")
	} else {
		fmt.Println("an error occured")
	}

	//Create table in Postgres of zip code outliers with fields zip code, total num requests, request languages, total eng requests, total other requests,
	//% english speaking, %spanish speaking, %other_indoeuropean, %asianpacific, %other, chiSquare value, ln(chiSquare) value, sum of std deviation of eng compared
	//to census and other language compared to corresponding language in census

} //END MAIN

func readCensus() *map[string][]string {

	//open DB
	pg_string := "postgres://postgres:DiipLplHoK7teB9o@35.185.109.176/postgres"
	db, err := sql.Open("postgres", pg_string)
	if err != nil {
		log.Fatal(err)
	} else {
		fmt.Println("database opened successfully")
	}
	defer db.Close()

	//query DB
	rows, err := db.Query(`SELECT zipcode, only_english_percent, spanish_percent, other_indoeuropean_percent, asianpacific_percent, other_lang_percent FROM zip_codes ORDER BY zipcode;`)
	if err != nil {
		log.Fatal(err)
	} else {
		fmt.Println("Query successful")
	}
	defer rows.Close()

	//SCAN all no_english_at_home values with their corresponding zip codes into a map
	cZips := make(map[string][]string)

	for rows.Next() {

		var F Fields

		err := rows.Scan(&F.zip, &F.only_english, &F.spanish, &F.other_indoeuropean, &F.asian_pacific, &F.other)
		if err != nil {
			log.Fatal(err)
		}

		fields := []sql.NullString{F.only_english, F.spanish, F.other_indoeuropean, F.asian_pacific, F.other}

		sFields := NullStringToString(fields)

		cZips[F.zip] = sFields

	}
	err = rows.Err()
	if err != nil {
		log.Fatal(err)
	}

	return &cZips

}

func NullStringToString(fields []sql.NullString) []string {

	var sFields []string
	for i := 0; i < len(fields); i++ {
		if fields[i].Valid == false {
			sFields = append(sFields, "nil")
		} else {
			sFields = append(sFields, fields[i].String)
		}
	}
	return sFields

}

func readJson() *map[string]map[string]string {

	jsonFile, _ := os.Open("result.json")
	defer jsonFile.Close()

	byteValue, _ := ioutil.ReadAll(jsonFile)

	var rZips map[string]map[string]string

	err := json.Unmarshal(byteValue, &rZips)
	if err != nil {
		log.Fatal(err)
	}

	return &rZips

}

func getExpected(current string, c []string, langs []string, n map[string][]string) *[]float64 {

	var expected []float64

	for i := 0; i < len(langs); i++ {
		switch langs[i] {
		case "en":
			expected = append(expected, calcExpected(c[0], current, n))
		case "en-US":
			expected = append(expected, calcExpected(c[0], current, n))
		case "es":
			expected = append(expected, calcExpected(c[1], current, n))
		case "sv":
			expected = append(expected, calcExpected(c[1], current, n))
		case "pt":
			expected = append(expected, calcExpected(c[2], current, n))
		case "ar":
			expected = append(expected, calcExpected(c[2], current, n))
		case "it":
			expected = append(expected, calcExpected(c[2], current, n))
		case "fr":
			expected = append(expected, calcExpected(c[2], current, n))
		case "de":
			expected = append(expected, calcExpected(c[2], current, n))
		case "ru":
			expected = append(expected, calcExpected(c[2], current, n))
		case "tr":
			expected = append(expected, calcExpected(c[2], current, n))
		case "bg":
			expected = append(expected, calcExpected(c[2], current, n))
		case "hu":
			expected = append(expected, calcExpected(c[2], current, n))
		case "uk":
			expected = append(expected, calcExpected(c[2], current, n))
		case "zh":
			expected = append(expected, calcExpected(c[3], current, n))
		case "id":
			expected = append(expected, calcExpected(c[3], current, n))
		case "ko":
			expected = append(expected, calcExpected(c[3], current, n))
		case "th":
			expected = append(expected, calcExpected(c[3], current, n))
		case "ja":
			expected = append(expected, calcExpected(c[3], current, n))
		default:
			expected = append(expected, calcExpected(c[4], current, n))
		}

	}

	return &expected

}

func calcExpected(num string, current string, n map[string][]string) float64 {

	temp := getFloat(num)

	perc := temp / 100

	dof, err := strconv.Atoi(n[current][0])
	if err != nil {
		log.Fatal(err)
	}

	val1 := getFloat(n[current][dof+1])
	val2 := getFloat(n[current][dof+2])

	total := val1 + val2

	expected := perc * total

	return expected

}

func getObserved(current string, expected []float64, n map[string][]string) *[]float64 {

	var observed []float64

	dof, err := strconv.Atoi(n[current][0])
	if err != nil {
		log.Fatal(err)
	}

	observed = append(observed, getFloat(n[current][dof+1]))
	observed = append(observed, getFloat(n[current][dof+2]))

	return &observed

}

func findMatches(r map[string]map[string]string, c map[string][]string) *[]string {

	var matches []string
	for k1 := range r {
		for k2 := range c {
			if k1 == k2 {
				matches = append(matches, k1)
			}
		}
	}
	return &matches

}

func findOutlierMatches(cs map[string]float64, stds map[string]float64) *[]string {

	var matches []string
	for k1 := range cs {
		for k2 := range stds {
			if k1 == k2 {
				matches = append(matches, k1)
			}
		}
	}

	return &matches

}

func createMap(m []string, r map[string]map[string]string) *map[string][]string {

	ma := make(map[string][]string)
	length := len(m)

	for i := 0; i < length; i++ {

		current := m[i]
		var B BidFields
		var langs []string
		var eng string
		var other string
		B.zip = current
		tMap := r[current]

		for k, v := range tMap {
			langs = append(langs, k)
			if k == "en" {
				eng = v
			} else {
				other = v
			}

			B.languages = langs
			B.dof = strconv.Itoa(len(B.languages))

		}
		if len(langs) > 1 {
			B.eng_total = eng
			B.lang2_total = other
		} else {
			if langs[0] == "en" {
				B.eng_total = eng
				B.lang2_total = "0"
			} else {
				B.eng_total = "0"
				B.lang2_total = other
			}
		}

		var fields []string
		fields = append(fields, B.dof)
		fields = append(fields, B.languages...)
		fields = append(fields, B.eng_total)
		fields = append(fields, B.lang2_total)

		ma[B.zip] = fields

	}
	return &ma

}

func getFloat(num1 string) float64 {

	if num1 == "" {
		return -1
	} else if num1 == "nil" {
		return -1
	}

	n1, err := strconv.ParseFloat(num1, 64)
	if err != nil {
		log.Fatal(err)
	}
	return n1

}

func getTwoLangZips(n map[string][]string) *[]string {

	var twoLangs []string

	for k, v := range n {
		if len(v) > 4 {
			twoLangs = append(twoLangs, k)
		}
	}

	return &twoLangs

}

func calcSTD(twoLangs []string, n map[string][]string, c map[string][]string) *map[string]float64 {

	stds := make(map[string]float64)

	var current string
	for i := 0; i < len(twoLangs); i++ {

		current = twoLangs[i]

		var lang1 []float64
		var lang2 []float64
		dof, err := strconv.Atoi(n[current][0])
		if err != nil {
			log.Fatal(err)
		}

		//you are appending totals, not percentages
		val1 := getFloat(n[current][dof+1])
		val2 := getFloat(n[current][dof+2])

		obs1 := getPercent(val1, dof, n[current])
		obs2 := getPercent(val2, dof, n[current])

		lang1 = append(lang1, obs1)
		lang2 = append(lang2, obs2)

		var langs []string
		langs = append(langs, n[current][1])
		langs = append(langs, n[current][2])
		if langs[0] != "en" {
			if langs[1] == "en" {
				temp := langs[0]
				langs[0] = langs[1]
				langs[1] = temp
			}
		}

		expected := *getSTDExpected(current, c[current], langs, n)

		lang1 = append(lang1, expected[0])
		fmt.Println(lang1)
		lang2 = append(lang2, expected[1])
		fmt.Println(lang2)

		_, std1 := gn.MeanStdDev(lang1, nil)
		_, std2 := gn.MeanStdDev(lang2, nil)

		total := std1 + std2

		stds[current] = total

	}

	return &stds

}

func getPercent(num float64, dof int, n []string) float64 {
	otherNum := getFloat(n[dof+1])
	if num == otherNum {
		otherNum = getFloat(n[dof+2])
	}
	total := num + otherNum
	perc := num / total
	return perc * 100
}

func getSTDExpected(current string, c []string, langs []string, n map[string][]string) *[]float64 {
	var expected []float64

	for i := 0; i < len(langs); i++ {
		switch langs[i] {
		case "en":
			expected = append(expected, getFloat(c[0]))
		case "en-US":
			expected = append(expected, getFloat(c[0]))
		case "es":
			expected = append(expected, getFloat(c[1]))
		case "sv":
			expected = append(expected, getFloat(c[1]))
		case "pt":
			expected = append(expected, getFloat(c[2]))
		case "ar":
			expected = append(expected, getFloat(c[2]))
		case "it":
			expected = append(expected, getFloat(c[2]))
		case "fr":
			expected = append(expected, getFloat(c[2]))
		case "de":
			expected = append(expected, getFloat(c[2]))
		case "ru":
			expected = append(expected, getFloat(c[2]))
		case "tr":
			expected = append(expected, getFloat(c[2]))
		case "bg":
			expected = append(expected, getFloat(c[2]))
		case "hu":
			expected = append(expected, getFloat(c[2]))
		case "uk":
			expected = append(expected, getFloat(c[2]))
		case "zh":
			expected = append(expected, getFloat(c[3]))
		case "id":
			expected = append(expected, getFloat(c[3]))
		case "ko":
			expected = append(expected, getFloat(c[3]))
		case "th":
			expected = append(expected, getFloat(c[3]))
		case "ja":
			expected = append(expected, getFloat(c[3]))
		default:
			expected = append(expected, getFloat(c[4]))
		}

	}

	return &expected

}

func prepSTD(m map[string]float64) *[]float64 {
	var stds []float64
	for _, v := range m {
		if v != math.Inf(1) && v > 0 {
			stds = append(stds, v)
		}
	}
	return &stds
}

//chi square value accentuates the difference b/w observed and expected by squaring this difference
//the maximum a chi square value can be is exactly the sum of expected values, right?
func performChiSquare(twoLangs []string, n map[string][]string, c map[string][]string) *map[string]float64 {

	chiSquares := make(map[string]float64)

	var current string
	//expected
	for i := 0; i < len(twoLangs); i++ {
		var langs []string
		current = twoLangs[i]

		langs = append(langs, n[current][1])
		langs = append(langs, n[current][2])

		if langs[0] != "en" || langs[0] != "en-US" {
			if langs[1] == "en" || langs[1] == "en-US" {
				temp := langs[0]
				langs[0] = langs[1]
				langs[1] = temp
			}
		}

		expected := *getExpected(current, c[current], langs, n)

		observed := *getObserved(current, expected, n)

		if len(expected) == len(observed) {
			cs := gn.ChiSquare(observed, expected)
			chiSquares[current] = cs
		}

	}

	return &chiSquares

}

func getOutliersCS(m map[string]float64, vals []float64) *map[string]float64 {

	ma := make(map[string]float64)

	third := getThirdQuartile(vals)

	for k, v := range m {
		if math.Log(v) > third {
			ma[k] = v
		}
	}

	return &ma
}

func getOutliersSTD(m map[string]float64, vals []float64) *map[string]float64 {

	ma := make(map[string]float64)

	third := getThirdQuartile(vals)

	for k, v := range m {
		if v > third {
			ma[k] = v
		}
	}

	return &ma
}

func calcMean(f map[string]float64) float64 {

	length := len(f)
	var total float64 = 0
	for _, v := range f {
		if v != math.Inf(0) {
			total += v
		}
	}
	mean := total / float64(length)

	return mean

}

func printData(m map[string]float64) {

	for k, v := range m {
		fmt.Print(k + ": ")
		fmt.Print(v)
		fmt.Println("")
	}
}

//Take natural log of each chisquare val
func prepHistogram(m map[string]float64) *[]float64 {
	var cs []float64
	for _, v := range m {
		if v != math.Inf(1) && v > 0 {
			cs = append(cs, math.Log(v))
		}
	}
	return &cs
}

func plotHistogram(vals []float64) {

	length := len(vals)

	v := make(pltr.Values, length)
	for i := range v {
		v[i] = vals[i]
	}

	p, err := plt.New()
	if err != nil {
		log.Fatal(err)
	}
	p.Title.Text = "Distribution of Languages in Bid Requests compared to Census Data by Zip Code"

	p.X.Label.Text = "Natural Log of Chi Square Values"

	p.Y.Label.Text = "Density"

	h, err := pltr.NewHist(v, 16)
	if err != nil {
		log.Fatal(err)
	}

	h.Normalize(1)
	p.Add(h)

	if err := p.Save(6*vg.Inch, 4*vg.Inch, "hist1.png"); err != nil {
		log.Fatal(err)
	}

}

func SelSort(vals []float64) *[]float64 {

	length := len(vals)
	for i := 0; i < length; i++ {
		j := i
		for j > 0 && vals[j] < vals[j-1] {
			vals[j], vals[j-1] = vals[j-1], vals[j]
			j -= 1
		}
	}
	return &vals
}

func getThirdQuartile(sorted []float64) float64 {

	median := getMedianIndex(sorted)
	third := getMedianIndex(sorted[median:]) + median
	return sorted[third]

}

func getMedianIndex(sorted []float64) int {

	length := len(sorted)
	var mid int

	if length%2 != 0 {
		mid = length / 2
	} else {
		mid = (length / 2) - 1
	}

	return mid

}

//ERROR ON FIRST INSERT!! SOMETHING TO DO WITH THE LANGUAGE FIELD
//MAYBE SOMETHING WITH A SPACE EXISTING AND VAR CHAR DATA TYPE
//3 separate tables for chisq, stdd, and both
func writeToDB(c map[string][]string, n map[string][]string, oCS map[string]float64, oSTD map[string]float64, outliers []string) bool {
	pg_string := "insert string here"
	db, err := sql.Open("postgres", pg_string)
	if err != nil {
		log.Fatal(err)
		return false
	} else {
		fmt.Println("database opened successfully")
	}
	defer db.Close()

	//query DB
	query, err := db.Query(`CREATE TABLE zip_code_shared_outliers (zip_code VARCHAR PRIMARY KEY, total_requests VARCHAR, request_langs VARCHAR, total_eng_requests VARCHAR, ` +
		`total_lang2_requests VARCHAR, census_english_perc VARCHAR, census_spanish_perc VARCHAR, census_indoeuropean_perc VARCHAR, census_asianpacific_perc VARCHAR, ` +
		`census_other_perc VARCHAR, chi_squared VARCHAR, sum_std_dev VARCHAR);`)
	if err != nil {
		log.Fatal(err)
		return false
	} else {
		fmt.Println("table created.")
	}
	defer query.Close()

	//pull in data from different maps and insert them into db
	var values []string

	for a := 0; a < len(outliers); a++ {
		var F DBFields
		current := outliers[a]
		F.zip = current
		dof, _ := strconv.Atoi(n[current][0])
		F.totalRequests = strconv.FormatFloat(getFloat(n[current][dof+1])+getFloat(n[current][dof+2]), 'f', 2, 64)
		F.requestLangs = getLangString(n[current][dof-1], n[current][dof])
		F.engTotal = strconv.FormatFloat(getFloat(n[current][dof+1]), 'f', 2, 64)
		F.otherTotal = strconv.FormatFloat(getFloat(n[current][dof+2]), 'f', 2, 64)

		F.cEngPercent = c[current][0]
		F.cSpanishPercent = c[current][1]
		F.cIndoEuroPerc = c[current][2]
		F.cAsianPac = c[current][3]
		F.cOther = c[current][4]

		F.chiSq = strconv.FormatFloat(oCS[current], 'f', 2, 64)
		F.sumStdDev = strconv.FormatFloat(oSTD[current], 'f', 2, 64)

		element := `(` + F.zip + `, ` + F.totalRequests + `, ` + F.requestLangs + `, ` + F.engTotal + `, ` + F.otherTotal + `, ` +
			F.cEngPercent + `, ` + F.cSpanishPercent + `, ` + F.cIndoEuroPerc + `, ` + F.cAsianPac + `, ` + F.cOther + `, ` +
			F.chiSq + `, ` + F.sumStdDev + `)`

		values = append(values, element)

	}

	statement := `INSERT INTO zip_code_shared_outliers (zip_code, total_requests, request_langs, total_eng_requests, total_lang2_requests, census_english_perc, census_spanish_perc, ` +
		`census_indoeuropean_perc, census_asianpacific_perc, census_other_perc, chi_squared, sum_std_dev) VALUES `

	start := 0
	lim := 1000
	for i := 0; i < len(values); i++ {
		//add 10 elements at a time
		for j := start; j < lim; j += 10 {
			//if, in last iteration, len(values)- limit is not divisible by 10.
			if j+10 > lim {
				for k := j; k <= lim; k++ {

					if k == lim-1 {
						break
					}

					_, err = db.Exec(statement + values[k] + ";")
					if err != nil {
						fmt.Println(statement + values[k])
						fmt.Println(err.Error())
						return false
					}

				}
			} else {
				//add 10 elements at a time
				_, err = db.Exec(statement + values[j] + "," + values[j+1] + "," + values[j+2] + "," + values[j+3] + "," + values[j+4] + "," + values[j+5] + "," + values[j+6] + "," + values[j+7] + "," + values[j+8] + "," + values[j+9] + ";")
				if err != nil {
					fmt.Println(statement + values[j] + "," + values[j+1] + "," + values[j+2] + "," + values[j+3] + "," + values[j+4] + "," + values[j+5] + "," + values[j+6] + "," + values[j+7] + "," + values[j+8] + "," + values[j+9] + ";")
					fmt.Println(err.Error())
					return false
				}

				start = lim
				if (lim + 10000) >= len(values) {
					lim = len(values)
				} else {
					lim = lim + 10000
				}
			}
		}
	}

	_, err = db.Exec(`COMMIT;`)
	if err != nil {
		log.Fatal(err)
		return false
	}

	return true

}

func getLangString(s1 string, s2 string) string {
	return s1 + " " + s2
}

func displayMap(m map[string][]string) {

	for k, v := range m {
		fmt.Print(k + ": ")
		fmt.Print(v)
		fmt.Println("")
	}

}

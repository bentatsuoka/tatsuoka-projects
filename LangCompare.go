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
	"os"
	"strconv"

	_ "github.com/lib/pq"
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
	zip           string
	languages     []string
	eng_percent   string
	lang2_percent string
}

func main() {

	//map of # of requests by language by zip code
	rMap := *readJson()

	//map of zip codes and the & of each language spoken in that zip code, from english, spanish, indo_euro, asian, other
	cMap := *readCensus()

	//compare maps and append the matched zip code keys to a slice (6276 matches)
	matches := *findMatches(rMap, cMap)

	//create new map of bid request zip codes mapped to a slice with languages then % of bids in those languages (limit 2 languages)
	pMap := *createMap(matches, rMap)

	displayMap(pMap)

} //END MAIN

func readCensus() *map[string][]string {

	//open DB
	pg_string := "insert db string"
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

	jsonFile, _ := os.Open("langData.json")
	defer jsonFile.Close()

	byteValue, _ := ioutil.ReadAll(jsonFile)

	var rZips map[string]map[string]string

	err := json.Unmarshal(byteValue, &rZips)
	if err != nil {
		log.Fatal(err)
	}

	return &rZips

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

		}
		if len(langs) > 1 {
			B.eng_percent = calcPercent(eng, other)
			B.lang2_percent = calcPercent(other, eng)
		} else {
			if langs[0] == "en" {
				B.eng_percent = "100"
				B.lang2_percent = "0"
			} else {
				B.eng_percent = "0"
				B.lang2_percent = "100"
			}
		}

		var fields []string
		fields = append(fields, B.languages...)
		fields = append(fields, B.eng_percent)
		fields = append(fields, B.lang2_percent)

		ma[B.zip] = fields

	}
	return &ma

}

func calcPercent(num1 string, num2 string) string {

	n1, _ := strconv.Atoi(num1)
	fmt.Println(n1)
	n2, _ := strconv.Atoi(num2)
	fmt.Println(n2)
	total := n1 + n2
	fmt.Println(total)
	var perc float64 = float64(n1) / float64(total)
	fmt.Println(perc)
	var tPerc float64 = perc * 100
	sPerc := strconv.FormatFloat(tPerc, 'f', 2, 64)
	fmt.Println(sPerc)
	return sPerc

}

func displayMap(m map[string][]string) {

	for k, v := range m {
		fmt.Print(k + ": ")
		fmt.Print(v)
		fmt.Println("")
	}

}

package main

import (
	"bufio"
	"database/sql"
	"encoding/csv"
	"fmt"
	"io"
	"log"
	"os"
	"strings"
	"sync"

	_ "github.com/lib/pq"
)

type dataPair struct {
	estimate      string
	marginOfError string
}

type Totals struct {
	//population by age
	totalPop               dataPair
	underFive              dataPair
	FivetoSeventeen        dataPair
	EighteentoTwentyFour   dataPair
	TwentyFivetoFortyFour  dataPair
	FourtyFivetoFiftyFour  dataPair
	FiftyFivetoSixtyFour   dataPair
	SixtyFivetoSeventyFour dataPair
	overSeventyFive        dataPair
	medianAge              dataPair
	//gender
	male   dataPair
	female dataPair
	//race
	oneRace   dataPair
	white     dataPair
	black     dataPair
	amerInd   dataPair
	asian     dataPair
	pacIsland dataPair
	other     dataPair
	twoOrMore dataPair
	hispanic  dataPair
	onlyWhite dataPair
	//language
	popOverFive  dataPair
	nonEnglish   dataPair
	goodEnglish  dataPair
	lessThanGood dataPair
	//marital status
	popOverFifteen dataPair
	neverMarried   dataPair
	nowMarried     dataPair
	separated      dataPair
	widowed        dataPair
	//education
	popOverTwentyFive dataPair
	noHighSchool      dataPair
	highSchool        dataPair
	someCollege       dataPair
	bachelorsDeg      dataPair
	graduateDeg       dataPair
	//income past 12 months
	popOverFifteenIncome     dataPair
	OnetoTenK                dataPair
	TenKtoFifteenK           dataPair
	FifteenKtoTwentyFiveK    dataPair
	TwentyFiveKtoThirtyFiveK dataPair
	ThirtyFiveKtoFiftyK      dataPair
	FiftyKtoSixtyFiveK       dataPair
	SixtyFiveKtoSeventyFiveK dataPair
	overSeventyFiveK         dataPair
	medianIncome             dataPair
	//poverty status
	eligPop           dataPair
	belowHundred      dataPair
	HundredtoOneFifty dataPair
	overOneFifty      dataPair
	//percent allocated?
	citizenship  dataPair
	placeOfBirth dataPair
}

type inState struct {
	//population by age
	totalPop               dataPair
	underFive              dataPair
	FivetoSeventeen        dataPair
	EighteentoTwentyFour   dataPair
	TwentyFivetoFortyFour  dataPair
	FourtyFivetoFiftyFour  dataPair
	FiftyFivetoSixtyFour   dataPair
	SixtyFivetoSeventyFour dataPair
	overSeventyFive        dataPair
	medianAge              dataPair
	//gender
	male   dataPair
	female dataPair
	//race
	oneRace   dataPair
	white     dataPair
	black     dataPair
	amerInd   dataPair
	asian     dataPair
	pacIsland dataPair
	other     dataPair
	twoOrMore dataPair
	hispanic  dataPair
	onlyWhite dataPair
	//language
	popOverFive  dataPair
	nonEnglish   dataPair
	goodEnglish  dataPair
	lessThanGood dataPair
	//marital status
	popOverFifteen dataPair
	neverMarried   dataPair
	nowMarried     dataPair
	separated      dataPair
	widowed        dataPair
	//education
	popOverTwentyFive dataPair
	noHighSchool      dataPair
	highSchool        dataPair
	someCollege       dataPair
	bachelorsDeg      dataPair
	graduateDeg       dataPair
	//income past 12 months
	popOverFifteenIncome     dataPair
	OnetoTenK                dataPair
	TenKtoFifteenK           dataPair
	FifteenKtoTwentyFiveK    dataPair
	TwentyFiveKtoThirtyFiveK dataPair
	ThirtyFiveKtoFiftyK      dataPair
	FiftyKtoSixtyFiveK       dataPair
	SixtyFiveKtoSeventyFiveK dataPair
	overSeventyFiveK         dataPair
	medianIncome             dataPair
	//poverty status
	eligPop           dataPair
	belowHundred      dataPair
	HundredtoOneFifty dataPair
	overOneFifty      dataPair
	//percent allocated?
	citizenship  dataPair
	placeOfBirth dataPair
}

type outState struct {
	//population by age
	totalPop               dataPair
	underFive              dataPair
	FivetoSeventeen        dataPair
	EighteentoTwentyFour   dataPair
	TwentyFivetoFortyFour  dataPair
	FourtyFivetoFiftyFour  dataPair
	FiftyFivetoSixtyFour   dataPair
	SixtyFivetoSeventyFour dataPair
	overSeventyFive        dataPair
	medianAge              dataPair
	//gender
	male   dataPair
	female dataPair
	//race
	oneRace   dataPair
	white     dataPair
	black     dataPair
	amerInd   dataPair
	asian     dataPair
	pacIsland dataPair
	other     dataPair
	twoOrMore dataPair
	hispanic  dataPair
	onlyWhite dataPair
	//language
	popOverFive  dataPair
	nonEnglish   dataPair
	goodEnglish  dataPair
	lessThanGood dataPair
	//marital status
	popOverFifteen dataPair
	neverMarried   dataPair
	nowMarried     dataPair
	separated      dataPair
	widowed        dataPair
	//education
	popOverTwentyFive dataPair
	noHighSchool      dataPair
	highSchool        dataPair
	someCollege       dataPair
	bachelorsDeg      dataPair
	graduateDeg       dataPair
	//income past 12 months
	popOverFifteenIncome     dataPair
	OnetoTenK                dataPair
	TenKtoFifteenK           dataPair
	FifteenKtoTwentyFiveK    dataPair
	TwentyFiveKtoThirtyFiveK dataPair
	ThirtyFiveKtoFiftyK      dataPair
	FiftyKtoSixtyFiveK       dataPair
	SixtyFiveKtoSeventyFiveK dataPair
	overSeventyFiveK         dataPair
	medianIncome             dataPair
	//poverty status
	eligPop           dataPair
	belowHundred      dataPair
	HundredtoOneFifty dataPair
	overOneFifty      dataPair
	//percent allocated?
	citizenship  dataPair
	placeOfBirth dataPair
}

type outUS struct {
	//population by age
	totalPop               dataPair
	underFive              dataPair
	FivetoSeventeen        dataPair
	EighteentoTwentyFour   dataPair
	TwentyFivetoFortyFour  dataPair
	FourtyFivetoFiftyFour  dataPair
	FiftyFivetoSixtyFour   dataPair
	SixtyFivetoSeventyFour dataPair
	overSeventyFive        dataPair
	medianAge              dataPair
	//gender
	male   dataPair
	female dataPair
	//race
	oneRace   dataPair
	white     dataPair
	black     dataPair
	amerInd   dataPair
	asian     dataPair
	pacIsland dataPair
	other     dataPair
	twoOrMore dataPair
	hispanic  dataPair
	onlyWhite dataPair
	//language
	popOverFive  dataPair
	nonEnglish   dataPair
	goodEnglish  dataPair
	lessThanGood dataPair
	//marital status
	popOverFifteen dataPair
	neverMarried   dataPair
	nowMarried     dataPair
	separated      dataPair
	widowed        dataPair
	//education
	popOverTwentyFive dataPair
	noHighSchool      dataPair
	highSchool        dataPair
	someCollege       dataPair
	bachelorsDeg      dataPair
	graduateDeg       dataPair
	//income past 12 months
	popOverFifteenIncome     dataPair
	OnetoTenK                dataPair
	TenKtoFifteenK           dataPair
	FifteenKtoTwentyFiveK    dataPair
	TwentyFiveKtoThirtyFiveK dataPair
	ThirtyFiveKtoFiftyK      dataPair
	FiftyKtoSixtyFiveK       dataPair
	SixtyFiveKtoSeventyFiveK dataPair
	overSeventyFiveK         dataPair
	medianIncome             dataPair
	//poverty status
	eligPop           dataPair
	belowHundred      dataPair
	HundredtoOneFifty dataPair
	overOneFifty      dataPair
	//percent allocated?
	citizenship  dataPair
	placeOfBirth dataPair
}

type zipCode struct {
	id1      string
	id2      string
	label    string
	totals   *Totals
	instate  *inState
	outstate *outState
	outus    *outUS
}

var wg sync.WaitGroup

func main() {

	//Open database here

	zips := make(chan []string, 32989)

	file, _ := os.Open("censusdata.csv")

	var codes []zipCode
	reader := csv.NewReader(bufio.NewReader(file))
	reader.LazyQuotes = true

	//channel reaches end of file and reads everything, but not everything is copied into codes
	for i := 0; i <= 32989; i++ {
		wg.Add(1)
		zip, err := reader.Read()
		if err == io.EOF {
			break
		}

		go func() {

			var code zipCode

			if err != nil {
				log.Fatal(err)
			}

			zips <- zip

			code = *assignData(zip, code)
			codes = append(codes, code)

		}()

		go func(zips chan []string) {
			//waiting
			wg.Wait()
			//done waiting
			close(zips)
		}(zips)

	}

	wg.Done()

	//CONNECT TO DB, CREATE DB, CREATE TABLE
	pg_string := "postgres://postgres:DiipLplHoK7teB9o@35.185.109.176/postgres"
	db, err := sql.Open("postgres", pg_string)
	if err != nil {
		fmt.Println(err.Error())
	} else {
		fmt.Println("database opened successfully")
	}

	//create table in db
	//This create table command should be good

	query, err := db.Prepare(`CREATE TABLE ZIP_CODES (` +
		`ZIPCODE varchar PRIMARY KEY, ` +
		`TOTAL_POP varchar, ` +
		`MEDIAN_AGE varchar, ` +
		`MALE varchar, ` +
		`FEMALE varchar, ` +
		`ONE_RACE varchar, ` +
		`WHITE varchar, ` +
		`BLACK varchar, ` +
		`ASIAN varchar, ` +
		`HISPANIC varchar, ` +
		`ELIGIBLE_POP_LANGUAGE varchar, ` +
		`NO_ENGLISH_AT_HOME varchar, ` +
		`SPEAKS_ENG_OUTSIDE_HOME_WELL varchar, ` +
		`SPEAKS_ENG_OUTSIDE_HOME_POORLY varchar, ` +
		`ELIGIBLE_POP_EDUCATION varchar, ` +
		`INCOMPLETE_HIGH_SCHOOL varchar, ` +
		`GRADUATED_HIGH_SCHOOL varchar, ` +
		`INCOMPLETE_COLLEGE varchar, ` +
		`GRADUATED_COLLEGE varchar, ` +
		`GRAD_SCHOOL varchar, ` +
		`ELIGIBLE_POP_INCOME varchar, ` +
		`OVER_75K varchar, ` +
		`MEDIAN_INCOME varchar, ` +
		`ELIGIBLE_POP_POVERTY varchar, ` +
		`BELOW_POVERTY_LEVEL varchar, ` +
		`COMFORTABLY_ABOVE_POVERTY varchar` +
		`);`)
	if err != nil {
		fmt.Println(err.Error())
	}
	//execute command
	_, err = query.Exec()
	if err != nil {
		fmt.Println(err.Error())
	} else {
		fmt.Println("table created!")
	}

	//insert all data into table here
	var values []string
	//APPEND ALL DATA INTO A SINGLE STRING, BRING INSERT OUTSIDE OF LOOP
	for i := 2; i < len(codes); i++ {
		zip_code := checkField(codes[i].id2)
		total_pop := checkField(codes[i].totals.totalPop.estimate)
		median_age := checkField(codes[i].totals.medianAge.estimate)
		isMale := checkField(codes[i].totals.male.estimate)
		isFemale := checkField(codes[i].totals.female.estimate)
		one_race := checkField(codes[i].totals.oneRace.estimate)
		isWhite := checkField(codes[i].totals.white.estimate)
		isBlack := checkField(codes[i].totals.black.estimate)
		isAsian := checkField(codes[i].totals.asian.estimate)
		isHispanic := checkField(codes[i].totals.hispanic.estimate)
		eligible_pop_lang := checkField(codes[i].totals.popOverFive.estimate)
		no_english := checkField(codes[i].totals.nonEnglish.estimate)
		eng_well := checkField(codes[i].totals.goodEnglish.estimate)
		eng_poor := checkField(codes[i].totals.lessThanGood.estimate)
		eligible_pop_ed := checkField(codes[i].totals.popOverTwentyFive.estimate)
		no_high_school := checkField(codes[i].totals.noHighSchool.estimate)
		grad_hs := checkField(codes[i].totals.highSchool.estimate)
		some_college := checkField(codes[i].totals.someCollege.estimate)
		grad_college := checkField(codes[i].totals.bachelorsDeg.estimate)
		phd := checkField(codes[i].totals.graduateDeg.estimate)
		eligible_pop_inc := checkField(codes[i].totals.popOverFifteenIncome.estimate)
		over_seventyfivek := checkField(codes[i].totals.overSeventyFiveK.estimate)
		median_income := checkField(codes[i].totals.medianIncome.estimate)
		elig_pop_pov := checkField(codes[i].totals.eligPop.estimate)
		below_pov := checkField(codes[i].totals.belowHundred.estimate)
		comf_above := checkField(codes[i].totals.overOneFifty.estimate)

		element := `(` + zip_code + `, ` + total_pop + `, ` + median_age + `, ` + isMale + `, ` + isFemale + `, ` + one_race + `, ` + isWhite + `, ` +
			isBlack + `, ` + isAsian + `, ` + isHispanic + `, ` + eligible_pop_lang + `, ` + no_english + `, ` + eng_well + `, ` + eng_poor + `, ` +
			eligible_pop_ed + `, ` + no_high_school + `, ` + grad_hs + `, ` + some_college + `, ` + grad_college + `, ` + phd + `, ` + eligible_pop_inc + `, ` +
			over_seventyfivek + `, ` + median_income + `, ` + elig_pop_pov + `, ` + below_pov + `, ` + comf_above + `)`

		values = append(values, element)

	}

	//insert statement to be used by every values insert
	statement := `INSERT INTO ZIP_CODES (ZIPCODE, TOTAL_POP, MEDIAN_AGE, MALE, FEMALE, ONE_RACE, WHITE, BLACK, ASIAN, ` +
		`HISPANIC, ELIGIBLE_POP_LANGUAGE, NO_ENGLISH_AT_HOME, SPEAKS_ENG_OUTSIDE_HOME_WELL, SPEAKS_ENG_OUTSIDE_HOME_POORLY, ` +
		`ELIGIBLE_POP_EDUCATION, INCOMPLETE_HIGH_SCHOOL, GRADUATED_HIGH_SCHOOL, INCOMPLETE_COLLEGE, GRADUATED_COLLEGE, ` +
		`GRAD_SCHOOL, ELIGIBLE_POP_INCOME, OVER_75K, MEDIAN_INCOME, ELIGIBLE_POP_POVERTY, BELOW_POVERTY_LEVEL, COMFORTABLY_ABOVE_POVERTY) ` +
		`VALUES `

	//insert values into DB in groups of 10
	start := 0
	limit := 1000
	for j := 0; j < len(values); j++ {
		//if final loop of 10 is less than 10 elements long, then add the rest one by one
		for k := start; k < limit; k += 10 {

			if k+10 > limit {
				for l := k; l <= limit; l++ {

					if l == limit-1 {
						break
					}
					fmt.Println(statement + values[l])

					_, err = db.Exec(statement + values[l] + ";")
					if err != nil {
						fmt.Println(statement + values[l])
						fmt.Println(err.Error())
					}

				}
			} else {
				//add 10 elements at a time
				fmt.Println(statement + values[k] + "," + values[k+1] + "," + values[k+2] + "," + values[k+3] + "," + values[k+4] + "," + values[k+5] + "," + values[k+6] + "," + values[k+7] + "," + values[k+8] + "," + values[k+9])

				_, err = db.Exec(statement + values[k] + "," + values[k+1] + "," + values[k+2] + "," + values[k+3] + "," + values[k+4] + "," + values[k+5] + "," + values[k+6] + "," + values[k+7] + "," + values[k+8] + "," + values[k+9] + ";")
				if err != nil {
					fmt.Println(statement + values[k] + "," + values[k+1] + "," + values[k+2] + "," + values[k+3] + "," + values[k+4] + "," + values[k+5] + "," + values[k+6] + "," + values[k+7] + "," + values[k+8] + "," + values[k+9] + ";")
					fmt.Println(err.Error())
				}

				start = limit
				if (limit + 10000) >= len(values) {
					limit = len(values)
				} else {
					limit = limit + 10000
				}
			}

		}
	}
	fmt.Println(len(values))
	fmt.Println(statement + values[0])

	_, err = db.Exec("COMMIT;")
	if err != nil {
		log.Fatal(err)
	}

	//close db
	db.Close()

}

//this fails if a datafield has a - and a , for example
func checkField(data string) string {
	if data == "-" {
		return "NULL"
	}
	if strings.Contains(data, ";") {
		str := strings.Replace(data, ";", "", -1)
		if strings.Contains(str, "-") {
			str1 := strings.Replace(str, "-", "", -1)
			if strings.Contains(str1, ",") {
				return strings.Replace(str1, ",", "", -1)
			} else {
				return str1
			}
		} else {
			return str
		}
	}
	if strings.Contains(data, "-") {
		str := strings.Replace(data, "-", "", -1)
		if strings.Contains(str, ",") {
			return strings.Replace(str, ",", "", -1)
		} else {
			return str
		}
	}
	if strings.Contains(data, ",") {
		return strings.Replace(data, ",", "", -1)
	} else {
		return data
	}
}

//this returns a zipCode object with corresponding data fields
func assignData(zip []string, obj zipCode) *zipCode {
	obj = zipCode{
		id1:   zip[0],
		id2:   zip[1],
		label: zip[2],
		totals: &Totals{
			//population
			totalPop:               dataPair{zip[3], zip[4]},
			underFive:              dataPair{zip[11], zip[12]},
			FivetoSeventeen:        dataPair{zip[19], zip[20]},
			EighteentoTwentyFour:   dataPair{zip[27], zip[28]},
			TwentyFivetoFortyFour:  dataPair{zip[35], zip[36]},
			FourtyFivetoFiftyFour:  dataPair{zip[43], zip[44]},
			FiftyFivetoSixtyFour:   dataPair{zip[51], zip[52]},
			SixtyFivetoSeventyFour: dataPair{zip[59], zip[60]},
			overSeventyFive:        dataPair{zip[67], zip[68]},
			medianAge:              dataPair{zip[75], zip[76]},
			//gender
			male:   dataPair{zip[83], zip[84]},
			female: dataPair{zip[91], zip[92]},
			//race
			oneRace:   dataPair{zip[99], zip[100]},
			white:     dataPair{zip[107], zip[108]},
			black:     dataPair{zip[115], zip[116]},
			amerInd:   dataPair{zip[123], zip[124]},
			asian:     dataPair{zip[131], zip[132]},
			pacIsland: dataPair{zip[139], zip[140]},
			other:     dataPair{zip[147], zip[148]},
			twoOrMore: dataPair{zip[155], zip[156]},
			hispanic:  dataPair{zip[163], zip[164]},
			onlyWhite: dataPair{zip[171], zip[172]},
			//language
			popOverFive:  dataPair{zip[179], zip[180]},
			nonEnglish:   dataPair{zip[187], zip[188]},
			goodEnglish:  dataPair{zip[195], zip[196]},
			lessThanGood: dataPair{zip[203], zip[204]},
			//marital status
			popOverFifteen: dataPair{zip[211], zip[212]},
			neverMarried:   dataPair{zip[219], zip[220]},
			nowMarried:     dataPair{zip[227], zip[228]},
			separated:      dataPair{zip[235], zip[236]},
			widowed:        dataPair{zip[243], zip[244]},
			//education
			popOverTwentyFive: dataPair{zip[251], zip[252]},
			noHighSchool:      dataPair{zip[259], zip[260]},
			highSchool:        dataPair{zip[267], zip[268]},
			someCollege:       dataPair{zip[275], zip[276]},
			bachelorsDeg:      dataPair{zip[283], zip[284]},
			graduateDeg:       dataPair{zip[291], zip[292]},
			//income
			popOverFifteenIncome:     dataPair{zip[299], zip[300]},
			OnetoTenK:                dataPair{zip[307], zip[308]},
			TenKtoFifteenK:           dataPair{zip[315], zip[316]},
			FifteenKtoTwentyFiveK:    dataPair{zip[323], zip[324]},
			TwentyFiveKtoThirtyFiveK: dataPair{zip[331], zip[332]},
			ThirtyFiveKtoFiftyK:      dataPair{zip[339], zip[340]},
			FiftyKtoSixtyFiveK:       dataPair{zip[347], zip[348]},
			SixtyFiveKtoSeventyFiveK: dataPair{zip[355], zip[356]},
			overSeventyFiveK:         dataPair{zip[363], zip[364]},
			medianIncome:             dataPair{zip[371], zip[372]},
			//poverty
			eligPop:           dataPair{zip[379], zip[380]},
			belowHundred:      dataPair{zip[387], zip[388]},
			HundredtoOneFifty: dataPair{zip[395], zip[396]},
			overOneFifty:      dataPair{zip[403], zip[404]},
			//percent allocated
			citizenship:  dataPair{zip[411], zip[412]},
			placeOfBirth: dataPair{zip[419], zip[420]},
		},
		instate: &inState{
			//population
			totalPop:               dataPair{zip[5], zip[6]},
			underFive:              dataPair{zip[13], zip[14]},
			FivetoSeventeen:        dataPair{zip[21], zip[22]},
			EighteentoTwentyFour:   dataPair{zip[29], zip[30]},
			TwentyFivetoFortyFour:  dataPair{zip[37], zip[38]},
			FourtyFivetoFiftyFour:  dataPair{zip[45], zip[46]},
			FiftyFivetoSixtyFour:   dataPair{zip[53], zip[54]},
			SixtyFivetoSeventyFour: dataPair{zip[61], zip[62]},
			overSeventyFive:        dataPair{zip[69], zip[70]},
			medianAge:              dataPair{zip[77], zip[78]},
			//gender
			male:   dataPair{zip[85], zip[86]},
			female: dataPair{zip[93], zip[94]},
			//race
			oneRace:   dataPair{zip[101], zip[102]},
			white:     dataPair{zip[109], zip[110]},
			black:     dataPair{zip[117], zip[118]},
			amerInd:   dataPair{zip[125], zip[126]},
			asian:     dataPair{zip[133], zip[134]},
			pacIsland: dataPair{zip[141], zip[142]},
			other:     dataPair{zip[149], zip[150]},
			twoOrMore: dataPair{zip[157], zip[158]},
			hispanic:  dataPair{zip[165], zip[166]},
			onlyWhite: dataPair{zip[173], zip[174]},
			//language
			popOverFive:  dataPair{zip[181], zip[182]},
			nonEnglish:   dataPair{zip[189], zip[190]},
			goodEnglish:  dataPair{zip[197], zip[198]},
			lessThanGood: dataPair{zip[205], zip[206]},
			//marital status
			popOverFifteen: dataPair{zip[213], zip[214]},
			neverMarried:   dataPair{zip[221], zip[222]},
			nowMarried:     dataPair{zip[229], zip[230]},
			separated:      dataPair{zip[237], zip[238]},
			widowed:        dataPair{zip[245], zip[246]},
			//education
			popOverTwentyFive: dataPair{zip[253], zip[254]},
			noHighSchool:      dataPair{zip[261], zip[262]},
			highSchool:        dataPair{zip[269], zip[270]},
			someCollege:       dataPair{zip[277], zip[278]},
			bachelorsDeg:      dataPair{zip[285], zip[286]},
			graduateDeg:       dataPair{zip[293], zip[294]},
			//income
			popOverFifteenIncome:     dataPair{zip[301], zip[302]},
			OnetoTenK:                dataPair{zip[309], zip[310]},
			TenKtoFifteenK:           dataPair{zip[317], zip[318]},
			FifteenKtoTwentyFiveK:    dataPair{zip[325], zip[326]},
			TwentyFiveKtoThirtyFiveK: dataPair{zip[333], zip[334]},
			ThirtyFiveKtoFiftyK:      dataPair{zip[341], zip[342]},
			FiftyKtoSixtyFiveK:       dataPair{zip[349], zip[350]},
			SixtyFiveKtoSeventyFiveK: dataPair{zip[357], zip[358]},
			overSeventyFiveK:         dataPair{zip[365], zip[366]},
			medianIncome:             dataPair{zip[373], zip[374]},
			//poverty
			eligPop:           dataPair{zip[381], zip[382]},
			belowHundred:      dataPair{zip[389], zip[390]},
			HundredtoOneFifty: dataPair{zip[397], zip[398]},
			overOneFifty:      dataPair{zip[405], zip[406]},
			//percent allocated
			citizenship:  dataPair{zip[413], zip[414]},
			placeOfBirth: dataPair{zip[421], zip[422]},
		},
		outstate: &outState{
			//population
			totalPop:               dataPair{zip[7], zip[8]},
			underFive:              dataPair{zip[15], zip[16]},
			FivetoSeventeen:        dataPair{zip[23], zip[24]},
			EighteentoTwentyFour:   dataPair{zip[31], zip[32]},
			TwentyFivetoFortyFour:  dataPair{zip[39], zip[40]},
			FourtyFivetoFiftyFour:  dataPair{zip[47], zip[48]},
			FiftyFivetoSixtyFour:   dataPair{zip[55], zip[56]},
			SixtyFivetoSeventyFour: dataPair{zip[63], zip[64]},
			overSeventyFive:        dataPair{zip[71], zip[72]},
			medianAge:              dataPair{zip[79], zip[80]},
			//gender
			male:   dataPair{zip[87], zip[88]},
			female: dataPair{zip[95], zip[96]},
			//race
			oneRace:   dataPair{zip[103], zip[104]},
			white:     dataPair{zip[111], zip[112]},
			black:     dataPair{zip[119], zip[120]},
			amerInd:   dataPair{zip[127], zip[128]},
			asian:     dataPair{zip[135], zip[136]},
			pacIsland: dataPair{zip[143], zip[144]},
			other:     dataPair{zip[151], zip[152]},
			twoOrMore: dataPair{zip[159], zip[160]},
			hispanic:  dataPair{zip[167], zip[168]},
			onlyWhite: dataPair{zip[175], zip[176]},
			//language
			popOverFive:  dataPair{zip[183], zip[184]},
			nonEnglish:   dataPair{zip[191], zip[192]},
			goodEnglish:  dataPair{zip[199], zip[200]},
			lessThanGood: dataPair{zip[207], zip[208]},
			//marital status
			popOverFifteen: dataPair{zip[215], zip[216]},
			neverMarried:   dataPair{zip[223], zip[224]},
			nowMarried:     dataPair{zip[231], zip[232]},
			separated:      dataPair{zip[239], zip[240]},
			widowed:        dataPair{zip[247], zip[248]},
			//education
			popOverTwentyFive: dataPair{zip[255], zip[256]},
			noHighSchool:      dataPair{zip[263], zip[264]},
			highSchool:        dataPair{zip[271], zip[272]},
			someCollege:       dataPair{zip[279], zip[280]},
			bachelorsDeg:      dataPair{zip[287], zip[288]},
			graduateDeg:       dataPair{zip[295], zip[296]},
			//income
			popOverFifteenIncome:     dataPair{zip[303], zip[304]},
			OnetoTenK:                dataPair{zip[311], zip[312]},
			TenKtoFifteenK:           dataPair{zip[319], zip[320]},
			FifteenKtoTwentyFiveK:    dataPair{zip[327], zip[328]},
			TwentyFiveKtoThirtyFiveK: dataPair{zip[335], zip[336]},
			ThirtyFiveKtoFiftyK:      dataPair{zip[343], zip[344]},
			FiftyKtoSixtyFiveK:       dataPair{zip[351], zip[352]},
			SixtyFiveKtoSeventyFiveK: dataPair{zip[359], zip[360]},
			overSeventyFiveK:         dataPair{zip[367], zip[368]},
			medianIncome:             dataPair{zip[375], zip[376]},
			//poverty
			eligPop:           dataPair{zip[383], zip[384]},
			belowHundred:      dataPair{zip[391], zip[392]},
			HundredtoOneFifty: dataPair{zip[399], zip[400]},
			overOneFifty:      dataPair{zip[407], zip[408]},
			//percent allocated
			citizenship:  dataPair{zip[415], zip[416]},
			placeOfBirth: dataPair{zip[423], zip[424]},
		},
		outus: &outUS{
			//population
			totalPop:               dataPair{zip[9], zip[10]},
			underFive:              dataPair{zip[17], zip[18]},
			FivetoSeventeen:        dataPair{zip[25], zip[26]},
			EighteentoTwentyFour:   dataPair{zip[33], zip[34]},
			TwentyFivetoFortyFour:  dataPair{zip[41], zip[42]},
			FourtyFivetoFiftyFour:  dataPair{zip[49], zip[50]},
			FiftyFivetoSixtyFour:   dataPair{zip[57], zip[58]},
			SixtyFivetoSeventyFour: dataPair{zip[65], zip[66]},
			overSeventyFive:        dataPair{zip[73], zip[74]},
			medianAge:              dataPair{zip[81], zip[82]},
			//gender
			male:   dataPair{zip[89], zip[90]},
			female: dataPair{zip[97], zip[98]},
			//race
			oneRace:      dataPair{zip[105], zip[106]},
			white:        dataPair{zip[113], zip[114]},
			black:        dataPair{zip[121], zip[122]},
			amerInd:      dataPair{zip[129], zip[130]},
			asian:        dataPair{zip[137], zip[138]},
			pacIsland:    dataPair{zip[145], zip[146]},
			other:        dataPair{zip[153], zip[154]},
			twoOrMore:    dataPair{zip[161], zip[162]},
			hispanic:     dataPair{zip[169], zip[170]},
			onlyWhite:    dataPair{zip[177], zip[178]},
			popOverFive:  dataPair{zip[185], zip[186]},
			nonEnglish:   dataPair{zip[193], zip[194]},
			goodEnglish:  dataPair{zip[201], zip[202]},
			lessThanGood: dataPair{zip[209], zip[210]},
			//marital status
			popOverFifteen: dataPair{zip[217], zip[218]},
			neverMarried:   dataPair{zip[225], zip[226]},
			nowMarried:     dataPair{zip[233], zip[234]},
			separated:      dataPair{zip[241], zip[242]},
			widowed:        dataPair{zip[249], zip[250]},
			//education
			popOverTwentyFive: dataPair{zip[257], zip[258]},
			noHighSchool:      dataPair{zip[265], zip[266]},
			highSchool:        dataPair{zip[273], zip[274]},
			someCollege:       dataPair{zip[281], zip[282]},
			bachelorsDeg:      dataPair{zip[289], zip[290]},
			graduateDeg:       dataPair{zip[297], zip[298]},
			//income
			popOverFifteenIncome:     dataPair{zip[305], zip[306]},
			OnetoTenK:                dataPair{zip[313], zip[314]},
			TenKtoFifteenK:           dataPair{zip[321], zip[322]},
			FifteenKtoTwentyFiveK:    dataPair{zip[329], zip[330]},
			TwentyFiveKtoThirtyFiveK: dataPair{zip[337], zip[338]},
			ThirtyFiveKtoFiftyK:      dataPair{zip[345], zip[346]},
			FiftyKtoSixtyFiveK:       dataPair{zip[353], zip[354]},
			SixtyFiveKtoSeventyFiveK: dataPair{zip[361], zip[362]},
			overSeventyFiveK:         dataPair{zip[369], zip[370]},
			medianIncome:             dataPair{zip[377], zip[378]},
			//poverty
			eligPop:           dataPair{zip[385], zip[386]},
			belowHundred:      dataPair{zip[393], zip[394]},
			HundredtoOneFifty: dataPair{zip[401], zip[402]},
			overOneFifty:      dataPair{zip[409], zip[410]},
			//percent allocated
			citizenship:  dataPair{zip[417], zip[418]},
			placeOfBirth: dataPair{zip[425], zip[426]},
		},
	}
	return &obj
}

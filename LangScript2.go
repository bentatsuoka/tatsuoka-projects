/*
Program that reads distribution of languages spoken by zip code from census csv and appends the distribution
of languages per each zip code into the ZIP_CODES db in postgres
*/
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
	_ "github.com/lib/pq"
)

type DataPair struct {
	total   string
	percent string
}

type Totals struct {
	totalPop         string
	onlyEnglish      DataPair
	otherThanEnglish *OtherThanEnglish
}

type OtherThanEnglish struct {
	pop               DataPair
	spanish           DataPair
	otherIndoEuropean DataPair
	asianPacific      DataPair
	other             DataPair
}

type ZipCode struct {
	zip    string
	totals *Totals
}

func main() {

	file, err := os.Open("LanguagesByZip.csv")
	if err != nil {
		log.Fatal(err)
	}

	reader := csv.NewReader(bufio.NewReader(file))
	reader.LazyQuotes = true

	var codes []ZipCode
	//33122 zip code objects
	limit := 33122
	for i := 0; i < limit; i++ {

		var code ZipCode

		line, err := reader.Read()
		if err != nil {
			log.Fatal(err)
		}
		if err == io.EOF {
			break
		}

		code = *assignData(line, code)

		codes = append(codes, code)

	}
	//check to see if codes was inserted correctly into zip_codes table
	added := insertIntoDB(codes)
	if added == true {
		fmt.Println("added")
	} else {
		fmt.Println("an error occured.")
	}

} //END MAIN

func assignData(line []string, obj ZipCode) *ZipCode {
	obj = ZipCode{
		zip: line[1],
		totals: &Totals{
			totalPop:    line[3],
			onlyEnglish: DataPair{line[15], line[17]},
			otherThanEnglish: &OtherThanEnglish{
				pop:               DataPair{line[27], line[29]},
				spanish:           DataPair{line[39], line[41]},
				otherIndoEuropean: DataPair{line[87], line[89]},
				asianPacific:      DataPair{line[135], line[137]},
				other:             DataPair{line[183], line[185]},
			},
		},
	}

	return &obj
}

func insertIntoDB(codes []ZipCode) bool {

	//open
	pg_string := "postgres://postgres:DiipLplHoK7teB9o@35.185.109.176/postgres"
	db, err := sql.Open("postgres", pg_string)
	if err != nil {
		fmt.Println(err.Error())
		return false
	} else {
		fmt.Println("database opened successfully")
	}
	defer db.Close()

	//add new columns to zip_codes
	query, err := db.Prepare(`ALTER TABLE ZIP_CODES ` +
		`ADD COLUMN ONLY_ENGLISH_TOTAL varchar, ADD COLUMN ONLY_ENGLISH_PERCENT varchar, ` +
		`ADD COLUMN OTHER_THAN_ENGLISH_TOTAL varchar, ADD COLUMN OTHER_THAN_ENGLISH_PERCENT varchar, ` +
		`ADD COLUMN SPANISH_TOTAL varchar, ADD COLUMN SPANISH_PERCENT varchar, ` +
		`ADD COLUMN OTHER_INDOEUROPEAN_TOTAL varchar, ADD COLUMN OTHER_INDOEUROPEAN_PERCENT varchar,` +
		`ADD COLUMN ASIANPACIFIC_TOTAL varchar, ADD COLUMN ASIANPACIFIC_PERCENT varchar, ` +
		`ADD COLUMN OTHER_LANG_TOTAL varchar, ADD COLUMN OTHER_LANG_PERCENT varchar;`)
	if err != nil {
		log.Fatal(err)
		return false
	}

	//execute alter table command
	_, err = query.Exec()
	if err != nil {
		log.Fatal(err)
		return false
	} else {
		fmt.Println("columns added to zip_codes.")
	}

	//insert data into new columns in zip_codes
	var values []string
	for i := 2; i < len(codes); i++ {
		zip_code := checkField(codes[i].zip)
		//total_pop := checkField(codes[i].totals.totalPop)
		only_english_total := checkField(codes[i].totals.onlyEnglish.total)
		only_english_percent := checkField(codes[i].totals.onlyEnglish.percent)
		non_english_total := checkField(codes[i].totals.otherThanEnglish.pop.total)
		non_english_percent := checkField(codes[i].totals.otherThanEnglish.pop.percent)
		spanish_total := checkField(codes[i].totals.otherThanEnglish.spanish.total)
		spanish_percent := checkField(codes[i].totals.otherThanEnglish.spanish.percent)
		other_indoeuro_total := checkField(codes[i].totals.otherThanEnglish.otherIndoEuropean.total)
		other_indoeuro_percent := checkField(codes[i].totals.otherThanEnglish.otherIndoEuropean.percent)
		asianPacific_total := checkField(codes[i].totals.otherThanEnglish.asianPacific.total)
		asianPacific_percent := checkField(codes[i].totals.otherThanEnglish.asianPacific.percent)
		other_lang_total := checkField(codes[i].totals.otherThanEnglish.other.total)
		other_lang_percent := checkField(codes[i].totals.otherThanEnglish.other.percent)

		element := `(` + zip_code + `, ` + only_english_total + `, ` + only_english_percent + `, ` +
			non_english_total + `, ` + non_english_percent + `, ` + spanish_total + `, ` + spanish_percent + `, ` +
			other_indoeuro_total + `, ` + other_indoeuro_percent + `, ` + asianPacific_total + `, ` + asianPacific_percent + `, ` +
			other_lang_total + `, ` + other_lang_percent + `)`

		values = append(values, element)

	}

	statement := `INSERT INTO ZIP_CODES (ZIPCODE, ONLY_ENGLISH_TOTAL, ONLY_ENGLISH_PERCENT, ` +
		`OTHER_THAN_ENGLISH_TOTAL, OTHER_THAN_ENGLISH_PERCENT, SPANISH_TOTAL, SPANISH_PERCENT, ` +
		`OTHER_INDOEUROPEAN_TOTAL, OTHER_INDOEUROPEAN_PERCENT, ASIANPACIFIC_TOTAL, ASIANPACIFIC_PERCENT, ` +
		`OTHER_LANG_TOTAL, OTHER_LANG_PERCENT) VALUES `

	endStatement := ` ON CONFLICT (ZIPCODE) DO UPDATE SET ONLY_ENGLISH_TOTAL = excluded.ONLY_ENGLISH_TOTAL, ONLY_ENGLISH_PERCENT = excluded.ONLY_ENGLISH_PERCENT, ` +
		`OTHER_THAN_ENGLISH_TOTAL = excluded.OTHER_THAN_ENGLISH_TOTAL, OTHER_THAN_ENGLISH_PERCENT = excluded.OTHER_THAN_ENGLISH_PERCENT, ` +
		`SPANISH_TOTAL = excluded.SPANISH_TOTAL, SPANISH_PERCENT = excluded.SPANISH_PERCENT, ` +
		`OTHER_INDOEUROPEAN_TOTAL = excluded.OTHER_INDOEUROPEAN_TOTAL, OTHER_INDOEUROPEAN_PERCENT = excluded.OTHER_INDOEUROPEAN_PERCENT, ` +
		`ASIANPACIFIC_TOTAL = excluded.ASIANPACIFIC_TOTAL, ASIANPACIFIC_PERCENT = excluded.ASIANPACIFIC_PERCENT, ` +
		`OTHER_LANG_TOTAL = excluded.OTHER_LANG_TOTAL, OTHER_LANG_PERCENT = excluded.OTHER_LANG_PERCENT`

	start := 0
	lim := 1000
	for j := 0; j < len(values); j++ {
		//add 10 elements at a time
		for k := start; k < lim; k += 10 {
			//if, in last iteration, len(values)- limit is not divisible by 10.
			if k+10 > lim {
				for l := k; l <= lim; l++ {

					if l == lim-1 {
						break
					}

					_, err = db.Exec(statement + values[l] + endStatement + ";")
					if err != nil {
						fmt.Println(statement + values[l] + endStatement + ";")
						fmt.Println(err.Error())
						return false
					}

				}
			} else {
				//add 10 elements at a time
				_, err = db.Exec(statement + values[k] + "," + values[k+1] + "," + values[k+2] + "," + values[k+3] + "," + values[k+4] + "," + values[k+5] + "," + values[k+6] + "," + values[k+7] + "," + values[k+8] + "," + values[k+9] + endStatement + ";")
				if err != nil {
					fmt.Println(statement + values[k] + "," + values[k+1] + "," + values[k+2] + "," + values[k+3] + "," + values[k+4] + "," + values[k+5] + "," + values[k+6] + "," + values[k+7] + "," + values[k+8] + "," + values[k+9] + endStatement + ";")
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

	//Commit changes
	_, err = db.Exec("COMMIT;")
	if err != nil {
		log.Fatal(err)
		return false
	}
	
	return true

}

func checkField(data string) string {
	if data == "" {
		return "NULL"
	}
	return string
}

func printCode(obj ZipCode) {
	fmt.Println("zip: " + obj.zip)
	fmt.Println("totalPop: " + obj.totals.totalPop)
	fmt.Println("totals.onlyEnglish.total: " + obj.totals.onlyEnglish.total)
	fmt.Println("totals.onlyEnglish.percent: " + obj.totals.onlyEnglish.percent)
	fmt.Println("totals.otherThanEnglish.pop.total: " + obj.totals.otherThanEnglish.pop.total)
	fmt.Println("totals.otherThanEnglish.pop.percent: " + obj.totals.otherThanEnglish.pop.percent)
	fmt.Println("totals.otherThanEnglish.spanish.total: " + obj.totals.otherThanEnglish.spanish.total)
	fmt.Println("totals.otherThanEnglish.spanish.percent: " + obj.totals.otherThanEnglish.spanish.percent)
	fmt.Println("totals.otherThanEnglish.otherIndoEuropean.total: " + obj.totals.otherThanEnglish.otherIndoEuropean.total)
	fmt.Println("totals.otherThanEnglish.otherIndoEuropean.percent: " + obj.totals.otherThanEnglish.otherIndoEuropean.percent)
	fmt.Println("totals.otherThanEnglish.asianPacific.total: " + obj.totals.otherThanEnglish.asianPacific.total)
	fmt.Println("totals.otherThanEnglish.asianPacific.percent: " + obj.totals.otherThanEnglish.asianPacific.percent)
	fmt.Println("totals.otherThanEnglish.other.total: " + obj.totals.otherThanEnglish.other.total)
	fmt.Println("totals.otherThanEnglish.other.percent: " + obj.totals.otherThanEnglish.other.percent)
	fmt.Println("")
}

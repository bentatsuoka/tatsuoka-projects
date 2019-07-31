//program that reads CSV with postal, language, and sum data for each zip code and creates a table by zip code with %english, $espanol etc...
package main

import (
	"bufio"
	"encoding/csv"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"os"
)

func main() {

	m := make(map[string]map[string]string)

	file, _ := os.Open("langCSV.csv")
	defer file.Close()

	reader := csv.NewReader(bufio.NewReader(file))
	reader.LazyQuotes = true

	//everything is a 2d array
	everything, err := reader.ReadAll()
	if err != nil {
		fmt.Println(err.Error())
	}

	m = *assignData(m, everything)

	jsonString, err := json.Marshal(m)
	if err != nil {
		log.Fatal(err)
	}

	err = ioutil.WriteFile("langData.json", jsonString, 0644)

} //END MAIN

func assignData(m map[string]map[string]string, file [][]string) *map[string]map[string]string {

	var visited []string
	for i := 0; i < len(file); i++ {
		oKey := file[i][0]
		iKey := file[i][1]
		iVal := file[i][2]

		if wasVisited(oKey, visited) == false {
			m[oKey] = map[string]string{iKey: iVal}
			visited = append(visited, oKey)
		} else {
			m[oKey][iKey] = iVal
		}
	}
	return &m

}

func wasVisited(key string, visited []string) bool {

	if len(visited) == 0 {
		return false
	}
	for i := 0; i < len(visited); i++ {
		if key == visited[i] {
			return true
		}
	}
	return false

}

func printZips(m map[string]map[string]string) {

	for i := range m {
		fmt.Printf("%s\n", i)
		for j := range m[i] {
			fmt.Printf("   %s: %s\n", j, m[i][j])
		}
	}

}

Instrukcja instalacji lokalnej bazy danych MongoDB

1. W celu poprawnego działania aplikacji należy zainstalować bazę danych MongoDB oraz zainicjalizować odpowiednią kolekcję.
2. Link do pobrania bazy danych: https://www.mongodb.com/download-center/community
3. Podczas instalacji nie zmieniaj żadnych domyślnych ustawień. Po instalacji powinna zainstalować się aplikacja pomocnicza MongoDB Compass, w której można tworzyć bazy, kolekcje oraz dokumenty, lecz tutorial będzie opierał się na komendach w Command Prompt. 
WAŻNE: Jeśli baza instalowana jest na systemie MacOS należy postępować wg tej instrukcji - https://docs.mongodb.com/manual/tutorial/install-mongodb-on-os-x/
4. Poprawność instalacji oraz działania bazy możemy sprawdzić w usługach systemu Windows. Wciśnij kombinację „Win + S” a następnie wpisz ‘Services’ i wybierz pierwszą opcję. Jeśli baza została włączona poprawnie usługa _MongoDB Server_ powinna mieć status _Running..._.
5. Jeśli baza jest zastopowana można spróbować ją włączyć klikając prawym przyciskiem myszy -> Start. 
W razie problemów załączam link do bardzo dokładnej dokumentacji MongoDB -> https://docs.mongodb.com/manual/tutorial/install-mongodb-on-windows/
6. Jeśli baza danych zainstalowała się poprawnie możemy przejść do folderu gdzie się zainstalowała. 

`"C:\Program Files\MongoDB\Server\4.0\bin\mongo.exe"`

Powyższa aplikacja stanowi terminal, który ma bezpośredni „kontakt” z bazą.
7. Możemy sprawdzić jakie bazy są stworzone domyślnie przez MongoDB wpisując komendę:
`show dbs`
Powinniśmy otrzymać coś w stylu:
```
admin          0.000GB
bazaTestowa    0.011GB
config         0.000GB
local          0.000GB
weatherData    0.003GB
weathercities  0.011GB
```
8. Następnie możemy przystąpić do stworzenia bazy danych aplikacji oraz kolekcji zawierającej dane dot. miast. W tym celu należy pobrać następujący plik typu .json
http://bulk.openweathermap.org/sample/city.list.json.gz
Typ .gz można otworzyć programem 7zip bądź WinRar. 
9. W celu ułatwienia procesu wypakuj plik city.list.json do folderu gdzie MongoDB został zainstalowany czyli:
`C:\Program Files\MongoDB\Server\4.0\bin`
10. Wciśnij `Win + s`, a następnie wpisz `Command prompt`. Otwórz terminal jako administrator.
```
Krótkie przypomnienie nawigacji w folderach:
dir – pokaż wszystkie pliki i foldery w obecnej lokalizacji (odpowiednik ls na MacOS),
cd [nazwa lokalizacji] – przejdź do danej lokalizacji,
cd.. – przejdź „do góry”

Hint: Podczas wpisywania nazwy folderu kliknij ‘Tab’ a terminal sam dokończy nazwę.
```
11. Przejdź do lokalizacji, gdzie znajduje się plik city.list.json. W tym celu można wykorzystać komendę:
`cd C:\Program Files\MongoDB\Server\4.0\bin`
12. Wpisz w Command Prompt (NIE w MongoDB!) następującą komendę:
`mongoimport --db weatherApp --collection cities --file city.list.json -–jsonArray`
Powinieneś uzyskać wynik poniżej, a w programie Mongo Compass powinna pojawić się baza `weatherApp`. 
```
C:\Program Files\MongoDB\Server\4.0\bin>mongoimport --db weatherApp --collection cities --file city.list.json --jsonArray
2018-12-21T19:26:30.917+0100    connected to: localhost
2018-12-21T19:26:32.896+0100    [######..................] weahterApp.cities       7.47MB/28.3MB (26.4%)
2018-12-21T19:26:35.897+0100    [################........] weahterApp.cities       19.1MB/28.3MB (67.6%)
2018-12-21T19:26:37.426+0100    [########################] weahterApp.cities       28.3MB/28.3MB (100.0%)
2018-12-21T19:26:37.426+0100    imported 209579 documents
```
13. Sprawdź w `mongo.exe` czy kolekcja wyświetla się wykonując po kolei poniższe komendy:
`use weatherApp`
`show collections`
`db.cities.find();`

Powinieneś otrzymać kilkadziesiąt dokumentów z danymi miast.
**Baza jest gotowa do użycia w aplikacji**


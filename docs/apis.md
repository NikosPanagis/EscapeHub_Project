//1.Wikidata API
**Τι δίνει: Μας δίνει δομημένες πληροφορίες για πρόσωπα,γεγονότα και έργα ταυτοποιημένα με μοναδικό κωδικό οντότητας (Q-ID)
**Παράδειγμα αιτήματος: https://www.wikidata.org/w/api.php?action=wbsearchentities&search=Mary%20Shelley&language=en&format=json
**Παράδειγμα απάντησης σε JSON:
```json
{
"searchinfo": {
"search": "Mary Shelley"
},
"search": [
  {
    "id": "Q47152",
    "title": "Q47152",
    "pageid": 49273,
    "concepturi": "https://www.wikidata.org/entity/Q47152",
    "repository": "wikidata",
    "url": "//www.wikidata.org/wiki/Q47152",
    "display": {
      "label": {
        "value": "Mary Shelley",
        "language": "en"
      },
      "description": {
        "value": "English writer (1797–1851)",
        "language": "en"
      }
    },
    "label": "Mary Shelley",
    "description": "English writer (1797–1851)",
    "match": {
      "type": "label",
      "language": "en",
      "text": "Mary Shelley"
    }
  }
]  
}
 ```
}
**Όρια και κανόνες:Σύμφωνα με την πολιτική User-Agent και το API etiquette της Wikimedia είναι υποχρεωτικό κάθε αίτημα να περιλαμβάνει περιγραφικό User-Agent αλλιώς ο server θα βγάλει σφάλμα. Δεν επιτρέπονται οι μαζικές παράλληλες κλήσεις.
**Τι μπορεί να πάει στραβά:Απόρριψη αιτήματος αν λείπει το σωστό User-Agent η και προσωρινός αποκλεισμός απο πολλά συνεχομένα αιτήματα.


//2.Open Library API
**Τι δίνει:Στην ουσία μας δίνει πληροφορίες για βιβλία μέσω αναζήτησης,έργων και συγγραφέων.Επιστρέφει πεδία όπως τίτλος('title'),όνομα συγγραφέα('author_name'),έτος πρώτης έκδοσης('first_publish_year') και μοναδικό κωδικό έργου ('key').
**Παράδειγμα αιτήματος:`https://openlibrary.org/search.json?q=the+lord+of+the+rings.
**Παράδειγμα απάντησης σε JSON:
```json
{
  "numFound": 1106,
  "start": 0,
  "numFoundExact": true,
  "num_found": 1106,
  "documentation_url": "https://openlibrary.org/dev/docs/api/search",
  "q": "the lord of the rings",
  "offset": null,
  "docs": [
    {
      "author_key": [
        "OL26320A"
      ],
      "author_name": [
        "J.R.R. Tolkien"
      ],
      "cover_edition_key": "OL51694024M",
      "cover_height": 731,
      "cover_i": 14625765,
      "cover_width": 394,
      "ebook_access": "borrowable",
      "edition_count": 253,
      "first_publish_year": 1954,
      "has_fulltext": true,
      "ia": [
        "lordofrings00tolk_5",
        "yzklerinefendisi0000jrrt",
        "hobbitguildpubli0000jrrt",
        "lordofrings0000tolk_k7n1",
        "lordofrings0000jrrt",
        "lordofrings0000tolk_o3c6",
        "lordofrings0000tolk_n6w1",
        "osenordosaneis0000tolk",
        "lordofrings0000tolk_d5h0",
        "lordofrings00tolk_2",
        "isbn_9780618343997",
        "snj.lordofrings0000jrrt_y7c7",
        "lordofringstrilo00jrrt",
        "derherrderringed0000jrrt",
        "lordofrings0000tolk_b2r2",
        "lordofringsfello0000jrrt",
        "isbn_9027456836",
        "lordofringsbcaed0000jrrt",
        "lordofrings0000tolk_y0v8",
        "lordofrings00tolk_3",
        "isbn_9780618640157",
        "lordofrings00tolk_1",
        "lordofringsonevo00jrrt",
        "lordofrings00tolk_0"
      ],
      "ia_collection": [
        "JaiGyan",
        "ServantsOfKnowledge-Print",
        "americana",
        "bannedbooks",
        "barryuniversity-ol",
        "binghamton-ol",
        "bostonpubliclibrary",
        "bplhoughton",
        "bpljordan-ol",
        "cnusd-ol",
        "cua-ol",
        "delawarecountydistrictlibrary",
        "inlibrary",
        "internetarchivebooks",
        "printdisabled",
        "riceuniversity-ol",
        "rochester-ol",
        "the-claremont-colleges-ol",
        "uiucdisabilityresources",
        "unb-ol",
        "uni-ol"
      ],
      "key": "/works/OL27448W",
      "language": [
        "fre",
        "tur",
        "bul",
        "cat",
        "spa",
        "swe",
        "ita",
        "pol",
        "jpn",
        "cze",
        "por",
        "ger",
        "glg",
        "eng",
        "rus",
        "dut",
        "slo",
        "kor"
      ],
      "lending_edition_s": "OL51711484M",
      "lending_identifier_s": "lordofrings00tolk_5",
      "public_scan_b": false,
      "series_key": [
        "OL330052L"
      ],
      "series_name": [
        "The Lord of the Rings"
      ],
      "series_position": [
        "1-3"
      ],
      "title": "The Lord of the Rings"
    }
  ]
}
```
**Όρια και κανόνες:Δεν απαιτείται API key.Σύμφωνα με την τεκμηρίωση του Open Library συνίσταται η αποστολή περιγραφικού User-Agent header ώστε να επιτρέπονται περισσότερα αιτήματα και να αποφεύγεται το rate limit.
**Τι μπορεί να πάει στραβά:Παρατήρησα ότι ο server καποίες φορές έχει αργή ανταπόκριση η ακομή και να μην έρθει το πεδίο first_publish_year σε κάποιες εγγραφές.


//3.Ποιό API για ποιον γρίφο
**Γρίφος 1:Ποιό είναι το έτος γέννησης η το Q-ID της Mary Shelley;**Το παίρνουμε από το πέδιο του wikidata api Q47152(γέννηση:1797).
**Γρίφος 2:Ποιό έτος ιδρύθηκε το The lord of the ring;**Το παίρνουμε από το πεδίο του Open Lbrary API first_publish_year(1954).
**Γρίφος 3:Ποιός είναι ο κωδικός έκδοσης του βιβλίου;**Το παίρνουμε από το πεδίο του Open Library API cover_edition_key(OL51694024M).

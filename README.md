# lab5_watki_JP
Lab5 - concurrent programming (threads)

# Treść programowa

Rozwiązanie wybranego problemu programowania współbieżnego z animacją jako formą prezentacji

# Cel zajęć

Celem zajęć jest implementacja gry **Game of Life** zaproponowanej przez brytyjskiego matematyka Johna Conwaya.

### Opis gry

Gra toczy się na nieskończonej dwu-wymiarowej planszy podzielonej na komórki. Każda komórka ma ośmiu sąsiadów oraz może być w dwóch stanach - “*żywa*” lub “*martwa*”. Stany komórek zmieniają się określonych interwałach czasu w zależności od stanu całej planszy. Po wykonaniu obliczeń, wszystkie komórki zmieniają swój stan dokładnie w tym samym momencie.

<aside>
💡

Bardziej dokładny opis oraz interaktywne wizualizacje można znaleźć w sekcji [Linki](https://www.notion.so/Linki-617f4aa4c9564c1586496da39e66b901?pvs=21).

</aside>

### Ewolucja komórek

Wykorzystamy standardowy zestaw reguł ([Conway 23/3](https://pl.wikipedia.org/wiki/Gra_w_%C5%BCycie)):

1. Martwa komórka, która ma dokładnie 3 żywych sąsiadów, staje się żywa w następnej jednostce czasu (*rodzi się*)
2. Żywa komórka z 2 lub 3 żywymi sąsiadami pozostaje nadal żywa; przy innej liczbie sąsiadów umiera (*samotność* / *zatłoczenie*)

# Projekt

Poniżej przestawiono wymagania dotyczące implementacji projektu.

<aside>

💡 **Przetwarzanie równoległe**

Prosta, sekwencyjna implementacja *Game of Life* może być zrealizowana za pomocą trzech pętli (jedna dla jednostek czasu, dwie pozostałe do iteracji po planszy). Warto jednak zauważyć, że obliczenie nowego stan każdej komórki na planszy jest niezależną operacją i może być przeprowadzone w dowolnej kolejności. Nie oznacza to jednak, że przetwarzania każdej komórki powinno odbywać się o osobnym wątku - to zbyt duża optymalizacja.

</aside>

<aside>
💡 Obszar gry (mapa 2D) powinna być zamodelowana jako **torus** - każda komórka posiada dokładnie osiem sąsiadów - szczególnie komórki na krawędziach.
  
```
x  @  x  _  _  _  _
x  x  x  _  _  _  _
_  _  _  _  _  _  _
_  _  _  _  _  _  _
_  _  _  _  _  _  _
_  _  _  _  _  _  _
x  x  x  _  _  _  _


W tym przypadku komórki `X` są sąsiadami komórki `@`.
```

</aside>

### Format pliku wejściowego
Plik wejściowy zawiera linie tekstu ASCII w następującej konwencji:

- L1-L3 określają rozmiar planszy oraz liczbę iteracji
- L4 określa liczbę współrzędnych żywych komórek
- L5-L* określa współrzędne kolejnych żywych komórek (punkt `0,0` znajduje się w lewym górnym rogu)

```
30
30
100
5
29 1
28 2
27 0
27 1
27 2
```

Należy zaimplementować kilka plików wejściowych demonstrujących działanie aplikacji ([stałe struktury, oscylatory, statki kosmiczne](https://en.wikipedia.org/wiki/Conway's_Game_of_Life#Examples_of_patterns)). Aplikacja powinna walidować poprawność pliku konfiguracyjnego.

## Obie grupy

### Partycjonowanie

Wątki powinny równolegle przetwarzać odpowiednie fragmenty planszy (partycje danych). W zależności od grupy będzie to podział na wiersze (”*row-based partitioning*”) lub kolumny (”*column-based partitioning*”). Dla przykładu, jeśli plansza ma rozmiar 8x8 a dostępne są 4 wątki, ich przyporządkowanie powinno wyglądać następująco:

```
row partitioning                        column partitioning
----------------                        ----------------
0 0 0 0 0 0 0 0                         0 0 1 1 2 2 3 3
0 0 0 0 0 0 0 0                         0 0 1 1 2 2 3 3
1 1 1 1 1 1 1 1                         0 0 1 1 2 2 3 3
1 1 1 1 1 1 1 1                         0 0 1 1 2 2 3 3
2 2 2 2 2 2 2 2                         0 0 1 1 2 2 3 3
2 2 2 2 2 2 2 2                         0 0 1 1 2 2 3 3
3 3 3 3 3 3 3 3                         0 0 1 1 2 2 3 3
3 3 3 3 3 3 3 3                         0 0 1 1 2 2 3 3
```

Proszę zwrócić uwagę, aby dystrybucja obciążenia była jak najbardziej równomierna.

### Synchronizacja wątków

Należy zadbać o synchronizację działania poszczególnych wątków, np. przetwarzanie kolejnego interwału czasowego może się zacząć w momencie zakończenia działania wszystkich wątków.

### Argumenty wejściowe

Minimalny zestaw argumentów wejściowych pobieranych przez aplikację:

- nazwę pliku z konfiguracją stanu początkowego (patrz wyżej),
- liczba dostępnych wątków

### Wizualizacja

Aplikacja umożliwia wygodną formę wizualizacji stanu planszy w kolejnych krokach czasowych (output w konsoli/GUI).

Należy dodatkowo zaproponować wizualizację działania mechanizmów współbieżnych. Każdy wątek powinien raportować następujące informacje:
1. Thread ID
2. Wiersze: (1) zakres początkowy, (2) zakres końcowy, (3) całkowita liczba przetworzonych wierszy
3. Kolumny: (4) zakres początkowy, (5) zakres końcowy, (6) całkowita liczba przetworzonych kolumn

```
# 6 threads, row-based partitioning
tid  0: rows:  0:16 (17) cols:  0:99 (100)
tid  1: rows: 17:33 (17) cols:  0:99 (100)
tid  3: rows: 51:67 (17) cols:  0:99 (100)
tid  2: rows: 34:50 (17) cols:  0:99 (100)
tid  4: rows: 68:83 (16) cols:  0:99 (100)
tid  5: rows: 84:99 (16) cols:  0:99 (100)
```

W przypadku wizualizacji GUI warto stosować kolory do oznaczenia komórek przetwarzanych przez poszczególne wątki.

## Grupa A

**Partycjonowanie**: Row-based

**Synchronizacja wątków**: mechanizm `CountDownLatch`

Wielowątkowość należy zaimplementować korzystając z dwóch struktur danych. Pierwsza służy do odczytu aktualnego stanu planszy. Druga jest aktualizowana. W momencie synchronizacji pracy wątków struktury należy podmienić.

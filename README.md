# Citation Extractor 

A simple command-line tool to automatically extract quotations (classical and Harvard-style) from a PDF file.

## Features

- Detects classical quotations between quotation marks and bloc-style quotation and associate them with their extracted footnote.
- Detects Harvard-style quotations (quotes + inline reference)
- Supports multi-page documents and truncated quotes
- Outputs results in `.txt` or `.pdf` format
- Lightweight and easy to use from the command line

## How to Use

### Prerequisites

- Java 21 or higher installed  
  Check with:

  ```bash
  java -version
  ````
### Download

1. Go to the [Releases](https://github.com/CamilleNerriere/citation-extractor/releases) tab.
2. Download the following files:
   - `citation-extractor.jar`
   - `extract.sh` (optional helper script)

### Basic Useage

If you downloaded `extract.sh`, make sure it is in the **same folder** as `citation-extractor.jar`.

#### Make the script executable

  ```bash
  chmod +x extract.sh
  ```

#### Then Run

  ```bash
  ./extract.sh <input-pdf-path> <output-path> <format>
  ```
For example : 

  ```bash
  ./extract.sh input/test.pdf output/result.txt txt
  ```

Available formats : 

- txt
- pdf

## Development 

Built with Java + Maven. Uses:

- Apache PDFBox
- SLF4J with Logback for logging

To build a runnable JAR with dependencies: 

```bash
mvn clean package -Pprod
```

Output : 

 ```
target/citation-extractor-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Licence 

MIT


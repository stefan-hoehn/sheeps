This little utility reads in Files from a Scanner that is used to scan Sheeps.
The scanner holds three files
- The total list of all sheeps named "*Zwillinge*"
- The list of died sheeps named "*ABGANG_TOD*"
- The list of sold sheeps name "*ABGANG_VK*"

- The utility reads in the list of all sheeps deducts the sold and died ones and creates a new updated list of all sheeps which is then written out.
- A backup of the old files is further more created in a separate folder each time before processing the files. 

BUILD 

> mvn package


RUN the utility 

>  java -cp target/Sheeps-1.0-SNAPSHOT.jar com.hoehn.sheeps.Sheeps
>  cp target/Sheeps-1.0-SNAPSHOT.jar ./sheeps.jar
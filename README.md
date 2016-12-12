  
  CONTENTS OF THIS FILE
  ---------------------
  
   * About
   * How to build
   * How to launch
  
  ABOUT
  -----
  
  HOW TO BUILD
  ------------
  Download and install [maven](https://maven.apache.org/)
  Execute `mvn clean install` in the project root directory
    
  HOW TO LAUNCH
  -------------
  To launch the program after build run 
  ```
  java -jar target/information-retrieval-1.0-SNAPSHOT-jar-with-dependencies.jar -s [feedURL]
  ```
  where feedURL is a link to RSS feed containing documents to be indexed, e.g. http://feeds.reuters.com/reuters/environment?format=xml
  
  After index is built, you will be able to run queries.

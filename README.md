# Light_Search

1) Crawler




2) Indexer
  1- Create Connection with two database of indexer % Crawler
  2- Create and start Threads
  3- retrieve Documents from Crawler
  4- for each link of Documents check if its already in indexer database or not if yes remove it else do nothing
  5- get html document of link
  6- remove tage - stop words 
  7- stemm words & count number of header & title & other pf each word in document
  8- create invertedfile using hashmap to calculate TF DF positions 
  9- create Documents that will be stored in database
  10- insert in database
  11- finally update Df for all words
  
 3) Query Processor






4) ranker




5) phrase search






6) interface



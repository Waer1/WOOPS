# Light_Search

# How to run Project
1) Open Eclipse then Open project springboot-first-app
2) Run SpringbootFirstAppApplication.java                               ----> Done Backend setup
3) open Folder ...\springboot-first-app\src\main\resources\GUI\src
4) open vs code in the above location
5) type in terminal :   npm start                                       -----> Done Frontend setup
# Note: you should have nodejs on your laptop in order to run react on

1) Crawler




2) Indexer
  1) Create Connection with two database of indexer % Crawler.
  2) Create and start Threads
  3) retrieve Documents from Crawler
  4) for each link of Documents check if its already in indexer database or not if yes remove it else do nothing
  5) get html document of link
  6) remove tage - stop words 
  7) stemm words & count number of header & title & other pf each word in document
  8) create invertedfile using hashmap to calculate TF DF positions 
  9) create Documents that will be stored in database
  10) insert in database
  11) finally update Df for all words
  
 3) Query Processor






4) ranker




5) phrase search






6) interface



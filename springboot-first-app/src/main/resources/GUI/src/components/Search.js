import React from "react";
import axios from "axios";
import Show from "./Show";
import ReactPaginate from "react-paginate";
import SearchField from "./SearchField";


const Search = (props) => {
  const postsPerPage = 10;
  var wordsinsearch = [];

  const goBack = () => {
    props.history.push("/");
  };
  
  const [wordEntered] = React.useState(
    props.location.wordEntered ? props.location.wordEntered : ""
  );
  wordsinsearch = wordEntered.trim().split(/\s+/);

  const [results, setResults] = React.useState([]);
  const [Numberofresults, setNumberofresults] = React.useState(0);

  React.useEffect(() => {
    async function getPosts() {
      if (props.location.wordEntered) {
        try {
          const response = await axios.get(
            `/data/${wordEntered}`
          );
          if (response) {
            setResults(response.data);
            setNumberofresults(response.data[0].SIZE);
          }
        } catch (error) {
          console.log(error);
        }
      }
    }
    getPosts();
  }, []);


  const searchGoogle = async (e) => {

    try {
      const response = await axios.get(
        `/data/${e}`
      );
      if (response) {
        setResults(response.data);
        setNumberofresults(response.data[0].SIZE);
        
      }
    } catch (error) {
      console.log(error);
    }
  };



const changePage = async (e) => {
          try {
          const response = await axios.get(
            `/page/${e.selected+1}`
          );
          if (response) {
            console.log("Done Sucessfully");
            setResults(response.data);
          }
        } catch (error) {
          console.log(error);
        } 
};



  return (
    <div className="search">
      <div className="search__form">
        <div className="search__form-logo">
          <img src="/images/small.png" alt="logo" onClick={goBack} />
        </div>
        <div className="Second_Form">
          <SearchField search={searchGoogle} />
        </div>
      </div>
      <div className="show__info">
        {Numberofresults !== 0 ? `Total results: ${Numberofresults}` : ""}
      </div>
      <Show results={results} Wordsinsearch={wordsinsearch}/>
      <ReactPaginate
        previousLabel={"Previous"}
        nextLabel={"Next"}
        pageCount={Math.ceil(Numberofresults / postsPerPage) }
        onPageChange={changePage}
        containerClassName={"paginationBttns"}
        previousLinkClassName={"previousBttn"}
        nextLinkClassName={"nextBttn"}
        disabledClassName={"paginationDisabled"}
        activeClassName={"paginationActive"}
      />
      
    </div>
  );
};

export default Search;

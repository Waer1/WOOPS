import React from "react";
import { FaSistrix, FaMicrophone } from "react-icons/fa";
import { key, cx } from "../API";
import axios from "axios";
import Show from "./Show";
import ReactPaginate from "react-paginate";


const Search = (props) => {
  const postsPerPage = 3;

  

  const goBack = () => {
    props.history.push("/");
  };
  
  const [wordEntered, setState] = React.useState(
    props.location.wordEntered ? props.location.wordEntered : ""
  );

  const [results, setResults] = React.useState([]);

  const searchGoogle = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.get(
        `https://www.googleapis.com/customsearch/v1?key=${key}&cx=${cx}&q=${wordEntered}`
      );
      if (response) {
        setResults(response.data);
        //setInfo(response.data.searchInformation);
      }
    } catch (error) {
      console.log(error);
    }
  };
  
const changePage = async (e) => {
  console.log(e.selected);
  
          try {
          const response = await axios.get(
            `/page/${e.selected+1}`
          );
          if (response) {
            console.log("Done Sucessfully");
            console.log(response);
            setResults(response.data);
          }
        } catch (error) {
          console.log(error);
        }

   
};

  React.useEffect(() => {
    async function getPosts() {
      console.log(wordEntered);
      if (props.location.wordEntered) {
        try {
          const response = await axios.get(
            `/data/${wordEntered}`
          );
          if (response) {
            setResults(response.data);
          }
        } catch (error) {
          console.log(error);
        }
      }
    }
    getPosts();
  }, []);

  return (
    <div className="search">
      <div className="search__form">
        <div className="search__form-logo">
          <img src="/images/small.png" alt="logo" onClick={goBack} />
        </div>
        <div className="search__form-input">
          <form className="home__form" onSubmit={searchGoogle}>
            <input
              type="text"
              className="home__input"
              value={wordEntered}
              onChange={(e) => setState(e.target.value)}
              required
            />

            <FaSistrix className="search__icon" />
            <FaMicrophone className="microphone" />
          </form>
        </div>
      </div>
      <Show results={results} />
      <ReactPaginate
        previousLabel={"Previous"}
        nextLabel={"Next"}
        pageCount={Math.ceil(results.SIZE /postsPerPage) }
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

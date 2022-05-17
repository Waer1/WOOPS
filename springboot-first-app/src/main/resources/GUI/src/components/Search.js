import React, {useState} from "react";
import axios from "axios";
import Show from "./Show";
import ReactPaginate from "react-paginate";
import SearchField from "./SearchField";
import Loading from "./Loading/Loading";
import Error from "./Error/Error";
import NoResult from "./NO result/NoResult";


const Search = (props) => {
  const postsPerPage = 10;

  const goBack = () => {
    props.history.push("/");
  };
  
  const [wordEntered] = React.useState(
    props.location.wordEntered ? props.location.wordEntered : ""
  );

  const [IsLoading, setIsLoading] = useState(1);
  const [Iserror, setIserror] = useState(0);
  const [results, setResults] = React.useState([]);
  const [Numberofresults, setNumberofresults] = React.useState(0);

  React.useEffect(() => {
    async function getPosts() {
      if (props.location.wordEntered) {
        setIsLoading(1);
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
          setIserror(1);
        }
    setIsLoading(0);
      }
    }
    getPosts();
  }, []);


  const searchGoogle = async (e) => {
    setIsLoading(1);
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
      setIserror(1);
    }
    setIsLoading(0);
  };


const changePage = async (e) => {
  setIsLoading(1);
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
          setIserror(1);
        } 
  setIsLoading(0);
};

const If = ({ condition, children }) => {
  if (condition) {
    return children;
  }else{
    return <></>
  }
};



  return (
    <div className="search">
      <div className="search__form">
        <div className="search__form-logo">
          <img src="" alt="logo" onClick={goBack} />
        </div>
        <div className="Second_Form">
          <SearchField search={searchGoogle} intialtext={wordEntered} />
        </div>
      </div>
      <If condition={IsLoading}><Loading /></If>
      <If condition={!IsLoading && !Iserror && Numberofresults > 0}>
      <div className="show__info">
        {Numberofresults !== 0 ? `Total results: ${Numberofresults}` : ""}
      </div>
        <Show results={results}/>
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
        </If>
        <If condition={!IsLoading && Iserror}>
          <Error />
        </If>
        <If condition={!IsLoading && !Iserror && Numberofresults === 0}>
          <NoResult />
        </If>

    </div>
  );
};

export default Search;
/*
      {!IsLoading && !Iserror && <Error />}

*/

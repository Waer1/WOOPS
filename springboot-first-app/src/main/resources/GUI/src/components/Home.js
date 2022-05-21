import React from "react";
import SearchField from "./SearchField.js";
import "./../App.css"

const Home = (props) => {

  const searchGoogle = (e) => {
    let wordEntered = e;
    props.history.push({ pathname: "/search", wordEntered });
  };

  return (
    <div className="home lightning">
      <div className="home__container">
        <div className="home__logo">
        </div>
        <SearchField search={searchGoogle} />
      </div>
    </div>
    
  );
};

export default Home;
/*
          <img src={require("../images/vectorpaint.svg")} alt="Logo" />

*/ 
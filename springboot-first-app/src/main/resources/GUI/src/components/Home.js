import React from "react";
import SearchField from "./SearchField.js";

const Home = (props) => {

  const searchGoogle = (e) => {
    let wordEntered = e;
    props.history.push({ pathname: "/search", wordEntered });
  };

  return (
    <div className="home">
      <div className="home__container">
        <div className="home__logo">
          <img src="/images/googleLogo.png" alt="Logo" />
        </div>
        <SearchField search={searchGoogle} />
      </div>
    </div>
    
  );
};

export default Home;
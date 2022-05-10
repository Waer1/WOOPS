import React from "react";

import { FaSistrix, FaMicrophone } from "react-icons/fa";

const Home = (props) => {
  const [state, setState] = React.useState("");
  const searchGoogle = (e) => {
    props.history.push({ pathname: "/search", state });
  };

  const getliklysearches =(e) =>{
    setState(e.target.value); 
    console.log(e.target.value);
  }

  return (

    <div className="home">
      <div className="home__container">
        <div className="home__logo">
          <img src="/images/googleLogo.png" alt="Logo" />
        </div>
        <form className="home__form" onSubmit={searchGoogle}>
          <input
            type="text"
            className="home__input"
            onChange={getliklysearches}
            value={state}
            required
          />
          <div className="home__group">
            <input type="submit" className="home__btn" value="Google Search" />
          </div>
          <FaSistrix className="search__icon" onClick={searchGoogle} />
          <FaMicrophone className="microphone" />
        </form>
      </div>
    </div>
    
  );
};

export default Home;
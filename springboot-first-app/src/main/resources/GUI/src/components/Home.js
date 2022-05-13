import React, {useState} from "react";
import axios from "axios";
import { FaSistrix, FaMicrophone } from "react-icons/fa";

const Home = (props) => {
  const [wordEntered, setWordEntered] = useState("");
  const [filteredData, setFilteredData] = useState([]);


  const searchGoogle = (e) => {
    console.log(wordEntered);
    props.history.push({ pathname: "/search", wordEntered });
    
  };

  
  const getliklysearches = async(e) =>{
    setWordEntered(e.target.value); 
    //console.log(e.target.value);
    const searchWord = e.target.value;
    setWordEntered(searchWord);



    if (searchWord === "") {
      setFilteredData([]);
      //console.log("i am empty");
    } else {
      //console.log("i am not  empty");
       
        //console.log("i am in suggestion");
          try {
            const response = await axios.get(
              `/suggestion/${searchWord}`
            );
            //console.log("i am waiting for response");
            if (response) {
              //console.log("Done Sucessfully");
              //console.log(response);
              setFilteredData(response.data);
            }
          } catch (error) {
            console.log(error);
          }
         // console.log("i have response");
        
      }
     

  }
  const Rerender = () => {
    this.forceUpdate()
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
            value={wordEntered}
            required
          />
          
        {filteredData.length !== 0 && (
        <div className="dataResult">
          {filteredData.slice(0, 15).map((value, key) => {
            return (
              <a className="dataItem" href={value.link} onClick={() =>{ setWordEntered(value);Rerender(); searchGoogle();} }>
                <p>{value}</p>
              </a>
            );
          })}
        </div>
        )}



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
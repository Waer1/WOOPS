import React, {useState, useEffect} from "react";
import axios from "axios";
import { FaSistrix } from "react-icons/fa";
import { TiMediaRecordOutline, TiMediaRecord } from "react-icons/ti";
import "./../App.css"

const SpeechRecognition =
  window.SpeechRecognition || window.webkitSpeechRecognition
const mic = new SpeechRecognition()

mic.continuous = true
mic.interimResults = true
mic.lang = 'en-US'


const SearchField = (props) => {


  const [wordEntered, setWordEntered] = useState(props.intialtext);
  const [filteredData, setFilteredData] = useState([]);
  const [isListening, setIsListening] = useState(0)
  const [Foucsed, setFoucsed] = useState(0);



  const searchGoogle = (e) => {
    e.preventDefault();
    props.search(wordEntered);
  };
  const clickonchose = (e) => {
    props.search(e);
  };

  const notrecord =  <TiMediaRecordOutline className="microphone"  onClick={() => setIsListening(1)} />;
  const record = <TiMediaRecord className="microphone"  onClick={() => setIsListening(0)} />;


  const getliklysearches = async(e) =>{
    setFoucsed(1);
    setWordEntered(e.target.value); 
    const searchWord = e.target.value;
    setWordEntered(searchWord);
    if (searchWord === "") {
      setFilteredData([]);
      setFoucsed(0);
    } else {
        connectandset(searchWord);
      }
  }

  const connectandset = async(text) =>{
    try {
        const response = await axios.get(
          `/suggestion/${text}`
        );
        if (response) {
          setFilteredData(response.data);
        }
      } catch (error) {
        console.log(error);
      }
  }


  useEffect(() => {
      if (isListening) {
        mic.start()
        mic.onend = () => {
          console.log('continue..')
          mic.start()
        }
      } else {
        mic.stop()
        mic.onend = () => {
          console.log('Stopped Mic on Click')
        }
      }
      mic.onstart = () => {
        console.log('Mics on')
      }
  
      mic.onresult = event => {
        const transcript = Array.from(event.results)
          .map(result => result[0])
          .map(result => result.transcript)
          .join('')
        connectandset(transcript)
        setWordEntered(transcript)
        mic.onerror = event => {
          console.log(event.error)
        }
    }
  }, [isListening])

  const If = ({ condition, children }) => {
    if (condition) {
      return children;
    }else{
      return <></>
    }
  };




  return (<>
        <form className="home__form" onSubmit={searchGoogle}>
          <input
            type="text"
            placeholder="search..."
            className={!Foucsed ? "home__input" : "home__input withoutborder"}
            onChange={getliklysearches}
            value={wordEntered}
            required
            autofocus
            onFocus={()=>{wordEntered !== props.intialtext ? setFoucsed(1) :setFoucsed(0); }}
            //onBlur={() => setFoucsed(0)}
          />
          <If condition={filteredData.length !== 0 && Foucsed}>
        <div className="dataResult">
          {Foucsed && filteredData.slice(0, 15).map((value, key) => {
            return (
              <a key={Math.random(0,5000)} className="dataItem" href={value.link} onClick={() =>{ setWordEntered(value); clickonchose(value)} }>
                <p>{value}</p>
              </a>
            );
          })}
        </div>
          </If>
          <div className="home__group">
            <input type="submit" className="home__btn" value="Google Search" />
            <button  className="clear__btn" onClick={() =>{setWordEntered("")} }>Clear Search</button>
          </div>

          <FaSistrix className="search__icon" onClick={searchGoogle} />
          {isListening === 0 ? notrecord : record}

        </form>
    </>
  );
};

export default SearchField;
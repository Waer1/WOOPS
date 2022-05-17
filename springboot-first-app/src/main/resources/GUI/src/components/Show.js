import React from "react";
import parse from "html-react-parser";

const Show = (props) => {
  const { results  } = props;
  const value = [...results];
  value.shift();
  return (
    <div className="show">
      {value.length > 0
        ? value.map((result) => (
            <div key={Math.random(0,5000)} className="show__details">
              <div className="show__link">
                <a href={result.URL}>{result.TITLE}</a>
              </div>
              <div className="show__title">
                <a href={result.URL}>{result.URL}</a>
              </div>
              <div className="show__description">
              <p>{parse(result.DESCRIPTION)}</p>
              </div>
            </div>
          ))
        : ""}
    </div>
  );
};

export default Show;
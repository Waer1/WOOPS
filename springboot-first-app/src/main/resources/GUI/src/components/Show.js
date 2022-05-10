import React from "react";

const Show = (props) => {
  const { results, info } = props;
  return (
    <div className="show">
      <div className="show__info">
        {info ? `Total results: ${info.totalResults}` : ""}
      </div>
      {results.length > 0
        ? results.map((result) => (
          //{ console.log(result)};
            <div className="show__details">
              <div className="show__link">
                <a href={result.TITLE}>{result.TITLE}</a>
              </div>
              <div className="show__title">
                <a href={result.URL}>{result.URL}</a>
              </div>
              <div className="show__description">
                <p>{result.DESCRIPTION}</p>
              </div>
            </div>
          ))
        : ""}
    </div>
  );
};

export default Show;

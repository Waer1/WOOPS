import { useState,useEffect } from "react";
import React from "react";
import "./error.css"
import "./../../App.css"

const Error = (props) => {
    function randomNum()
    {
        return Math.floor(Math.random() * 9)+1;
    }

        var loop1,loop2,loop3,time=30,
         i=0;
        useEffect(() => {
            loop3 = setInterval(function()
        {
            if(i > 120)
            {
                clearInterval(loop3);
                setThirdDigit( 4 );
            }else
            {
                setThirdDigit( randomNum() );
                i++;
            }
        }, time);
        loop2 = setInterval(function()
        {
            if(i > 60)
            {
                clearInterval(loop2);
                setSecondDigit(0);
            }else
            {
                setSecondDigit(randomNum());  
                i++;
            }
        }, time);
        loop1 = setInterval(function()
        {
            if(i > 140)
            {
                clearInterval(loop1);
                setFirstDigit(4)
            }else
            {
                setFirstDigit(randomNum())
                i++;
            }
        }, time);
          }, []);
        const [FirstDigit, setFirstDigit] = useState(0);
        const [SecondDigit, setSecondDigit] = useState(0);
        const [ThirdDigit, setThirdDigit] = useState(0);
    

  return (<>
            <div className="error">
                    <div  className="ground-color text-center">
                        <div  className="container-error-404">
                            <div  className="clip"><div  className="shadow"><span  className="digit thirdDigit">{ThirdDigit}</span></div></div>
                            <div  className="clip"><div  className="shadow"><span  className="digit secondDigit">{SecondDigit}</span></div></div>
                            <div  className="clip"><div  className="shadow"><span  className="digit firstDigit">{FirstDigit}</span></div></div>
                            <div  className="msg">OH!<span  className="triangle"></span></div>
                        </div>
                        <h2  className="h1">Sorry! Page not found</h2>
                    </div>
            </div>
  </>
  );
};

export default Error;
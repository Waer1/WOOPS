import { useState,useEffect } from "react";
import React from "react";
import "./NoResult.css"

const NoResult = (props) => {
    function randomNum()
    {
        return Math.floor(Math.random() * 9)+1;
    }

        var loop1,time=30,
         i=0;
        useEffect(() => {
        loop1 = setInterval(function()
        {
            if(i > 60)
            {
                clearInterval(loop1);
                setFirstDigit(0)
            }else
            {
                setFirstDigit(randomNum())
                i++;
            }
            console.log("aim out now fron 1")
        }, time);
          }, []);

        const [FirstDigit, setFirstDigit] = useState(0);
    

  return (<>
            <div class="error">
                <div class="container-floud">
                    <div class="col-xs-12 ground-color text-center">
                        <div class="container-error-404">
                            <div class="clip"><div class="shadow"><span class="digit firstDigit">{FirstDigit}</span></div></div>
                            <div class="msg">OH!<span class="triangle"></span></div>
                        </div>
                        <h2 class="h1">Sorry! We Cant found your result</h2>
                    </div>
                </div>
            </div>
  </>
  );
};

export default NoResult;
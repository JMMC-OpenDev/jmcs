
/**** MODULE   'mth' ****/
/****                 contains :                         ****/
/****    o DEFINE CONSTANTS                                ****/
/****    o ENUM CONSTANTS                                ****/
/****    o STRUCTURES                                    ****/
/****    o FUNCTIONS                                     ****/


if (!is_void(plug_in)) plug_in, "mth";
write,"mth plugin loaded";


/****** DEFINE CONSTANTS (numerical ones only) ******/

/****** ENUM CONSTANTS ******/

/****** STRUCTURES ******/

/****** FUNCTIONS ******/

/* 
 * Wrapping of 'mthLinInterp' function */   
  
extern __mthLinInterp;
/* PROTOTYPE
    int  mthLinInterp( int , pointer , pointer , int , pointer , pointer )
*/
/* DOCUMENT  mthLinInterp( int , pointer , pointer , int , pointer , pointer )
  * C-prototype:
    ------------
    int mthLinInterp  (int nbOfCurvePoints ,double *xList ,double *yList ,int nbOfPointsToInterp ,double *xToInterpList ,double *yInterpolatedList)
*/

#property  copyright "www,forex-tsd.com"
#property  link      "www,forex-tsd.com"

//---- indicator settings
#property indicator_separate_window
#property indicator_buffers 2
#property indicator_color1  LimeGreen
#property indicator_color2  Red
#property indicator_width1  2
#property indicator_width2  2
#property indicator_minimum 0
#property indicator_maximum 1


//---- indicator parameters
extern ENUM_TIMEFRAMES    TimeFrame            = PERIOD_CURRENT;
extern double             FastEMA              = 12;
extern double             SlowEMA              = 26;
extern double             SignalEMA            = 9;
extern ENUM_APPLIED_PRICE PriceToCross         = PRICE_CLOSE;
extern bool               alertsOn             = false;
extern bool               alertsOnCurrent      = true;
extern bool               alertsMessage        = true;
extern bool               alertsSound          = false;
extern bool               alertsEmail          = false;
extern bool               alertsPush           = false;
extern bool               verticalLinesVisible = false;
extern bool               linesOnFirst         = true;
extern string             verticalLinesID      = "mp Lines";
extern color              verticalLinesUpColor = DeepSkyBlue;
extern color              verticalLinesDnColor = PaleVioletRed;
extern ENUM_LINE_STYLE    verticalLinesStyle   = STYLE_DOT;
extern int                verticalLinesWidth   = 0;
//---- indicator buffers
double UpH[];
double DnH[];   
double Price[];
double cross[];
double predict[];
string indicatorFileName;
bool   returnBars;

//+------------------------------------------------------------------+
//| Custom indicator initialization function                         |
//+------------------------------------------------------------------+
int init()
  {
//---- drawing settings
   IndicatorBuffers(4);   
   SetIndexBuffer(0,UpH); SetIndexStyle(0,DRAW_HISTOGRAM);
   SetIndexBuffer(1,DnH); SetIndexStyle(1,DRAW_HISTOGRAM);   
   SetIndexBuffer(2,Price);
   SetIndexBuffer(3,cross);
 
      indicatorFileName = WindowExpertName();
      returnBars        = TimeFrame==-99;
      TimeFrame         = MathMax(TimeFrame,_Period);
   IndicatorShortName(timeFrameToString(TimeFrame)+" MACD("+FastEMA+","+SlowEMA+","+SignalEMA+")");
   SetIndexLabel(0,"MACD Predictor");

//---- initialization done
   return(0);
  }
int deinit()
{
   ObjectDelete("signal1");
   deleteLines();
   
return(0);
}
//+------------------------------------------------------------------+
//| Moving Averages Convergence/Divergence                           |
//+------------------------------------------------------------------+
int start()
  {
   double v1minus2divsgEMAplus1 = 1.0 - 2.0 / (SignalEMA + 1.0);
   double v1minus2divSEMAplus1  = 1.0 - 2.0 / (SlowEMA + 1.0);
   double v1minus2divFEMAplus1  = 1.0 - 2.0 / (FastEMA + 1.0);
   double FEMAmSEMAt2           = 2 * (FastEMA - SlowEMA);
   double FEMAp1tSEMAp1         = (FastEMA + 1.0)*(SlowEMA + 1.0);
   
   int counted_bars=IndicatorCounted();
   int limit;

   if(counted_bars<0) return(-1);
   if(counted_bars>0) counted_bars--;
         limit=MathMin(Bars-counted_bars,Bars-1);
         if (returnBars) { UpH[0] = MathMin(limit+1,Bars-1); return(0); }

   //
   //
   //
   //
   //
   
   if (TimeFrame == Period())
   {
      for (int i=limit;i>=0;i--)
      {
         double tpredict =(-1.0) / (FEMAmSEMAt2 * v1minus2divsgEMAplus1) * FEMAp1tSEMAp1 * ((-v1minus2divsgEMAplus1)*(iMA(NULL,0,FastEMA,0,1,PRICE_CLOSE,0)   * v1minus2divFEMAplus1 - iMA(NULL,0,SlowEMA,0,1,PRICE_CLOSE,0)   * v1minus2divSEMAplus1 ));// + iCustom(NULL,0,"DiNapoli MACD",FastEMA,SlowEMA,SignalEMA,false,0,0) * v1minus2divsgEMAplus1);
                Price[i] =(-1.0) / (FEMAmSEMAt2 * v1minus2divsgEMAplus1) * FEMAp1tSEMAp1 * ((-v1minus2divsgEMAplus1)*(iMA(NULL,0,FastEMA,0,1,PRICE_CLOSE,i+1) * v1minus2divFEMAplus1 - iMA(NULL,0,SlowEMA,0,1,PRICE_CLOSE,i+1) * v1minus2divSEMAplus1 ));// + iCustom(NULL,0,"DiNapoli MACD",FastEMA,SlowEMA,SignalEMA,false,0,i+1) * v1minus2divsgEMAplus1);
         double price    = iMA(NULL,0,1,0,MODE_SMA,PriceToCross,i);
         UpH[i]   = EMPTY_VALUE;
         DnH[i]   = EMPTY_VALUE;
         cross[i] = cross[i+1];
            if (Price[i]>price) cross[i] =-1;
            if (Price[i]<price) cross[i] = 1;
            if (cross[i] == 1)  UpH[i]   = 1;
            if (cross[i] ==-1)  DnH[i]   = 1;
            
           //
           //
           //
           //
           //
         
           if (verticalLinesVisible)
           {
             deleteLine(Time[i]);
             if (cross[i]!=cross[i+1])
             {
                if (cross[i] == 1) drawLine(i,verticalLinesUpColor);
                if (cross[i] ==-1) drawLine(i,verticalLinesDnColor);
             }
           }      
      //double predict=(-1.0)*1.0/((FastEMA-SlowEMA)*2*(1.0-2.0/(SignalEMA+1.0)))* (FastEMA+1.0)*(SlowEMA+1.0)*((2.0/(SignalEMA+1.0)-1.0)*(iMA(NULL,0,FastEMA,0,1,PRICE_CLOSE,0)*(1.0-2.0/(FastEMA+1.0))-iMA(NULL,0,SlowEMA,0,1,PRICE_CLOSE,0)*(1.0-2.0/(SlowEMA+1.0)))+iCustom(NULL,0,"DiNapoli MACD",FastEMA,SlowEMA,SignalEMA,false,0,0)*(1-2.0/(SignalEMA+1.0)));

    datetime futureBarTime  = Time[i]+TimeFrame*60;  
    datetime futureBarTime1 = Time[i];  
 
      ObjectCreate("signal1",OBJ_TREND,0,Time[i],Price[i],futureBarTime,tpredict);
         ObjectSet("signal1",OBJPROP_COLOR,Gold);
         ObjectSet("signal1",OBJPROP_RAY,false);
         ObjectSet("signal1",OBJPROP_STYLE,STYLE_DASH);
         double predict1=ObjectGet("signal1",OBJPROP_PRICE1);
      
    if(predict1<tpredict||predict1>tpredict)
    {
      ObjectDelete("signal1");
      
      ObjectCreate("signal1",OBJ_TREND,0,Time[i],Price[i],futureBarTime,tpredict);
         ObjectSet("signal1",OBJPROP_COLOR,Gold);
         ObjectSet("signal1",OBJPROP_RAY,false);
         ObjectSet("signal1",OBJPROP_STYLE,STYLE_DASH);
         predict1=ObjectGet("signal1",OBJPROP_PRICE1);}

   }
   manageAlerts();
   return(0);
   
  }
  
   limit = MathMax(limit,MathMin(Bars-1,iCustom(NULL,TimeFrame,indicatorFileName,-99,0,0)*TimeFrame/Period()));
   for (i=limit; i>=0; i--)
   {
      int y = iBarShift(NULL,TimeFrame,Time[i]);
         UpH[i] = iCustom(NULL,TimeFrame,indicatorFileName,PERIOD_CURRENT,FastEMA,SlowEMA,SignalEMA,PriceToCross,alertsOn,alertsOnCurrent,alertsMessage,alertsSound,alertsEmail,alertsPush,verticalLinesVisible,linesOnFirst,verticalLinesID,verticalLinesUpColor,verticalLinesDnColor,verticalLinesStyle,verticalLinesWidth,0,y);
         DnH[i] = iCustom(NULL,TimeFrame,indicatorFileName,PERIOD_CURRENT,FastEMA,SlowEMA,SignalEMA,PriceToCross,alertsOn,alertsOnCurrent,alertsMessage,alertsSound,alertsEmail,alertsPush,verticalLinesVisible,linesOnFirst,verticalLinesID,verticalLinesUpColor,verticalLinesDnColor,verticalLinesStyle,verticalLinesWidth,1,y);               
   }
return(0);
}

//-------------------------------------------------------------------
//
//-------------------------------------------------------------------
//
//
//
//
//

string sTfTable[] = {"M1","M5","M15","M30","H1","H4","D1","W1","MN"};
int    iTfTable[] = {1,5,15,30,60,240,1440,10080,43200};

string timeFrameToString(int tf)
{
   for (int i=ArraySize(iTfTable)-1; i>=0; i--) 
         if (tf==iTfTable[i]) return(sTfTable[i]);
                              return("");
}
//-------------------------------------------------------------------
//                                                                  
//-------------------------------------------------------------------
//
//
//
//
//

void manageAlerts()
{
   if (alertsOn)
   {
      if (alertsOnCurrent)
           int whichBar = 0;
      else     whichBar = 1; 
      if (cross[whichBar] != cross[whichBar+1])
      {
         if (cross[whichBar] ==  1) doAlert("up");
         if (cross[whichBar] == -1) doAlert("down");
      }
   }
}

//
//
//
//
//

void doAlert(string doWhat)
{
   static string   previousAlert="nothing";
   static datetime previousTime;
   string message;
   
   if (previousAlert != doWhat || previousTime != Time[0]) {
       previousAlert  = doWhat;
       previousTime   = Time[0];

       //
       //
       //
       //
       //

       message =  StringConcatenate(timeFrameToString(Period())+" "+Symbol()," at ",TimeToStr(TimeLocal(),TIME_SECONDS)," MACD predictor crossed price ",doWhat);
          if (alertsMessage) Alert(message);
          if (alertsEmail)   SendMail(Symbol()+" MACD predictor",message);
          if (alertsPush)    SendNotification(message);
          if (alertsSound)   PlaySound("alert2.wav");
   }
}

//
//
//
//
//

void drawLine(int i,color theColor)
{
      string name = verticalLinesID+":"+Time[i];
   
      //
      //
      //
      //
      //
      
      int add = 0; if (!linesOnFirst) add = _Period*60-1;
      ObjectCreate(name,OBJ_VLINE,0,Time[i]+add,0);
         ObjectSet(name,OBJPROP_COLOR,theColor);
         ObjectSet(name,OBJPROP_STYLE,verticalLinesStyle);
         ObjectSet(name,OBJPROP_WIDTH,verticalLinesWidth);
         ObjectSet(name,OBJPROP_BACK,true);
}

//
//
//
//
//

void deleteLines()
{
   string lookFor       = verticalLinesID+":";
   int    lookForLength = StringLen(lookFor);
   for (int i=ObjectsTotal()-1; i>=0; i--)
   {
      string objectName = ObjectName(i);
         if (StringSubstr(objectName,0,lookForLength) == lookFor) ObjectDelete(objectName);
   }
}

//
//
//
//
//

void deleteLine(datetime time)
{
   string lookFor = verticalLinesID+":"+time; ObjectDelete(lookFor);
}

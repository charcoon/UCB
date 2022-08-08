//+------------------------------------------------------------------+
//|                                                 forecast osc.mq4 |
//+------------------------------------------------------------------+
#property copyright "www.forex-station.com"
#property link      "www.forex-station.com"
#property indicator_separate_window
#property indicator_buffers 4
#property indicator_level1  0
#property indicator_color1  clrSteelBlue
#property indicator_color2  clrRed
#property indicator_color3  clrWhite
#property indicator_color4  clrWhite
#property strict

extern ENUM_TIMEFRAMES TimeFrame    = PERIOD_CURRENT;  // Time frame
input int              regress      = 15;              // Linear regression period
input int              t3           = 3;               // T3 period
input double           b            = 0.7;             // T3 Hot
input bool             OriginalT3   = false;           // Calculate Tim tillson way of T3?
input bool             ArrowOnFirst = true;            // Arrow on first bars
input bool             Interpolate  = true;            // Interpolate true/false?

double osc[],osct3[],hiSig[],loSig[],count[];
string indicatorFileName;
#define _mtfCall(_buff,_ind) iCustom(NULL,TimeFrame,indicatorFileName,PERIOD_CURRENT,regress,t3,b,OriginalT3,_buff,_ind)

//------------------------------------------------------------------
//
//------------------------------------------------------------------
int OnInit()
{
   IndicatorBuffers(5);
   SetIndexBuffer(0,osc,  INDICATOR_DATA);
   SetIndexBuffer(1,osct3,INDICATOR_DATA);
   SetIndexBuffer(2,hiSig,INDICATOR_DATA); SetIndexArrow(2,159); SetIndexStyle(2,DRAW_ARROW);
   SetIndexBuffer(3,loSig,INDICATOR_DATA); SetIndexArrow(3,159); SetIndexStyle(3,DRAW_ARROW);
   SetIndexBuffer(4,count,INDICATOR_CALCULATIONS);
   
   indicatorFileName = WindowExpertName();
   TimeFrame         = fmax(TimeFrame,_Period);
   
   IndicatorShortName(timeFrameToString(TimeFrame)+" Forecast oscillator"); 
return(INIT_SUCCEEDED);
}
void OnDeinit(const int reason)  {  }

//------------------------------------------------------------------
//
//------------------------------------------------------------------

int OnCalculate(const int rates_total,const int prev_calculated,const datetime &time[],
                const double &open[],
                const double &high[],
                const double &low[],
                const double &close[],
                const long &tick_volume[],
                const long &volume[],
                const int &spread[])
{
   int i,counted_bars=prev_calculated;
      if(counted_bars<0) return(-1);
      if(counted_bars>0) counted_bars--;
         int limit = fmin(rates_total-counted_bars,rates_total-regress); count[0] = limit;
         if (TimeFrame != _Period)
         {
            limit = (int)fmax(limit,fmin(rates_total-1,_mtfCall(9,0)*TimeFrame/_Period));
            for (i=limit;i>=0 && !_StopFlag; i--)
            {
                int y = iBarShift(NULL,TimeFrame,Time[i]);
                int x = y;
                if (ArrowOnFirst)
                      {  if (i<rates_total-1) x = iBarShift(NULL,TimeFrame,Time[i+1]);               }
                else  {  if (i>0)             x = iBarShift(NULL,TimeFrame,Time[i-1]); else x = -1;  }
                   osc[i]   = _mtfCall(0,y); 
                   osct3[i] = _mtfCall(1,y);  
                   loSig[i] = (x!=y) ? _mtfCall(2,y) : EMPTY_VALUE;
                   hiSig[i] = (x!=y) ? _mtfCall(3,y) : EMPTY_VALUE;  
                     
                   //
                   //
                   //
                   //
                   //
                     
                   if (!Interpolate || (i>0 && y==iBarShift(NULL,TimeFrame,Time[i-1]))) continue;
                   #define _interpolate(buff) buff[i+k] = buff[i]+(buff[i+n]-buff[i])*k/n
                   int n,k; datetime btime = iTime(NULL,TimeFrame,y);
                      for(n = 1; (i+n)<rates_total && Time[i+n] >= btime; n++) continue;	
                      for(k = 1; k<n && (i+n)<rates_total && (i+k)<rates_total; k++) 
                      {
                        _interpolate(osc);
                        _interpolate(osct3);
                      }                       
            }
   return(rates_total);
   } 

   //
   //
   //
   //
   //

   for(i = limit; i >= 0; i--)
   {
      double lr     = iLinr(Close[i],regress,i,rates_total);
         osc[i]     = 100*(Close[i]-lr)/lr;
         osct3[i]   = iT3(osc[i],t3,b,OriginalT3,i,rates_total);
         loSig[i+1] = EMPTY_VALUE;      
         hiSig[i+1] = EMPTY_VALUE;      
         if (osc[i+1] > osct3[i+2] && osc[i+2] <= osct3[i+3] && osct3[i+1]<0) loSig[i+1] = osct3[i+1]-0.05;
         if (osc[i+1] < osct3[i+2] && osc[i+2] >= osct3[i+3] && osct3[i+1]>0) hiSig[i+1] = osct3[i+1]+0.05;
   }
return(rates_total);
}

//------------------------------------------------------------------
//
//------------------------------------------------------------------
//
//
//
//


double workLinr[][1];
double iLinr(double price, double period, int r, int bars, int instanceNo=0)
{
   if (ArrayRange(workLinr,0)!= bars) ArrayResize(workLinr,bars); r = bars-r-1;

   //
   //
   //
   //
   //
   
      period = fmax(period,1);
      workLinr[r][instanceNo] = price;
         double lwmw = period; double lwma = lwmw*price;
         double sma  = price;
         for(int k=1; k<period && (r-k)>=0; k++)
         {
            double weight = period-k;
                   lwmw  += weight;
                   lwma  += weight*workLinr[r-k][instanceNo];  
                   sma   +=        workLinr[r-k][instanceNo];
         }             
   
   return(3.0*lwma/lwmw-2.0*sma/period);
}

//
//
//
//
//

#define t3Instances 1
double workT3[][t3Instances*6];
double workT3Coeffs[][6];
#define _tperiod 0
#define _c1      1
#define _c2      2
#define _c3      3
#define _c4      4
#define _alpha   5

//
//
//
//
//

double iT3(double price, double period, double hot, bool original, int i, int bars, int tinstanceNo=0)
{
   if (ArrayRange(workT3,0) != bars)                 ArrayResize(workT3,bars);
   if (ArrayRange(workT3Coeffs,0) < (tinstanceNo+1)) ArrayResize(workT3Coeffs,tinstanceNo+1);

   if (workT3Coeffs[tinstanceNo][_tperiod] != period)
   {
     workT3Coeffs[tinstanceNo][_tperiod] = period;
        double a = hot;
            workT3Coeffs[tinstanceNo][_c1] = -a*a*a;
            workT3Coeffs[tinstanceNo][_c2] = 3*a*a+3*a*a*a;
            workT3Coeffs[tinstanceNo][_c3] = -6*a*a-3*a-3*a*a*a;
            workT3Coeffs[tinstanceNo][_c4] = 1+3*a+a*a*a+3*a*a;
            if (original)
                 workT3Coeffs[tinstanceNo][_alpha] = 2.0/(1.0 + period);
            else workT3Coeffs[tinstanceNo][_alpha] = 2.0/(2.0 + (period-1.0)/2.0);
   }
   
   //
   //
   //
   //
   //
   
   int instanceNo = tinstanceNo*6;
   int r = bars-i-1;
   if (r == 0)
      {
         workT3[r][0+instanceNo] = price;
         workT3[r][1+instanceNo] = price;
         workT3[r][2+instanceNo] = price;
         workT3[r][3+instanceNo] = price;
         workT3[r][4+instanceNo] = price;
         workT3[r][5+instanceNo] = price;
      }
   else
      {
         workT3[r][0+instanceNo] = workT3[r-1][0+instanceNo]+workT3Coeffs[tinstanceNo][_alpha]*(price                  -workT3[r-1][0+instanceNo]);
         workT3[r][1+instanceNo] = workT3[r-1][1+instanceNo]+workT3Coeffs[tinstanceNo][_alpha]*(workT3[r][0+instanceNo]-workT3[r-1][1+instanceNo]);
         workT3[r][2+instanceNo] = workT3[r-1][2+instanceNo]+workT3Coeffs[tinstanceNo][_alpha]*(workT3[r][1+instanceNo]-workT3[r-1][2+instanceNo]);
         workT3[r][3+instanceNo] = workT3[r-1][3+instanceNo]+workT3Coeffs[tinstanceNo][_alpha]*(workT3[r][2+instanceNo]-workT3[r-1][3+instanceNo]);
         workT3[r][4+instanceNo] = workT3[r-1][4+instanceNo]+workT3Coeffs[tinstanceNo][_alpha]*(workT3[r][3+instanceNo]-workT3[r-1][4+instanceNo]);
         workT3[r][5+instanceNo] = workT3[r-1][5+instanceNo]+workT3Coeffs[tinstanceNo][_alpha]*(workT3[r][4+instanceNo]-workT3[r-1][5+instanceNo]);
      }

   //
   //
   //
   //
   //
   
   return(workT3Coeffs[tinstanceNo][_c1]*workT3[r][5+instanceNo] + 
          workT3Coeffs[tinstanceNo][_c2]*workT3[r][4+instanceNo] + 
          workT3Coeffs[tinstanceNo][_c3]*workT3[r][3+instanceNo] + 
          workT3Coeffs[tinstanceNo][_c4]*workT3[r][2+instanceNo]);
}

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

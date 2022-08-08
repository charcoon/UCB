var x{i in 1..12} integer >=0; # of new employees hired for month i
var b{i in 0..12} integer >=0; # of total employees have for month i
var a{i in 1..12}  >=0; # of hours worked each month
var c{i in 0..12}  >=0; # of hours fall short
var kc{i in 1..12} binary >=0; # dummy for c
var ka{i in 1..12} binary >=0; # dummy for a
set m := {1..12}; # of months
param phase1 {m} >=0; # of projects in phase 1 in month m
param phase2 {m} >=0; # of projects in phase 2 in month m
param phase3 {m} >=0; # of projects in phase 2 in month m

maximize grossmargin: sum {i in 1..12} ((a[i]*210)-(b[i]*156*105)-(210*c[i]*.01));

subject to
initialc:c[0]=0;
initialb:b[0]=35;
totaempl {i in 1..12}: b[i]=b[i-1]+x[i];


consta1 {i in 1..12}:  (b[i]*156) - ((760 * phase1[i])+(1153*phase2[i])+(106*phase3[i])+c[i-1]) <= (100000000*ka[i]);
consta2 {i in 1..12}:  ((760 * phase1[i])+(1153*phase2[i])+(106*phase3[i])+c[i-1]) - (b[i]*156) <= 100000000*(1-ka[i]);
consta3 {i in 1..12}: a[i] <= ((760 * phase1[i])+(1153*phase2[i])+(106*phase3[i])+c[i-1]);
consta4 {i in 1..12}: a[i] <= (b[i]*156);
consta5 {i in 1..12}: a[i] >= ((760 * phase1[i])+(1153*phase2[i])+(106*phase3[i])+c[i-1]) - (100000000*(1-ka[i]));
consta6 {i in 1..12}: a[i] >= (b[i]*156) - (100000000*ka[i]);


constc1 {i in 1..12}: ((760*phase1[i])+(1153*phase2[i])+(106*phase3[i])+(c[i-1])-(b[i]*156)) - (0) <= 1000000*(kc[i]);
constc2 {i in 1..12}: (0) - ((760*phase1[i])+(1153*phase2[i])+(106*phase3[i])+(c[i-1])-(b[i]*156)) <= 1000000*(1-kc[i]);
constc3 {i in 1..12}: c[i] >= ((760*phase1[i])+(1153*phase2[i])+(106*phase3[i])+(c[i-1])-(b[i]*156));
constc4 {i in 1..12}: c[i] >= (0);
constc5 {i in 1..12}: c[i] <= ((760*phase1[i])+(1153*phase2[i])+(106*phase3[i])+(c[i-1])-(b[i]*156)) + 1000000*(1-kc[i]);
constc6 {i in 1..12}: c[i] <= (0) + 1000000*(kc[i]);
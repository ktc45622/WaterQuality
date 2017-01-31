package weather.clientside.gui.administrator;

import java.util.ArrayList;

/**
 * The <code>DefaultStationVarProperties</code> class stores 
 * station properties. This class is used with the 
 * SpecifyWeatherStationWindow.java class.
 * @author Bloomsburg University Software Engineering
 * @author Fen Qin 2009
 * @author Xianrui Meng 2010
 * @version Spring 2010
 */
public class DefaultStationVarProperties {

    private VariablesProperties WindDir;
    private VariablesProperties WindSpd;
    private VariablesProperties WindGust;
    private VariablesProperties Humidity;
    private VariablesProperties TempIn;
    private VariablesProperties Temp;
    private VariablesProperties RawBarom;
    private VariablesProperties TotRain;
    private VariablesProperties ET;
    private VariablesProperties UV;
    private VariablesProperties Solar;
    private VariablesProperties WindChill;
    private VariablesProperties HeatIxIn;
    private VariablesProperties HeatIndex;
    private VariablesProperties DewPoint;
    private VariablesProperties BaromSL;
    private VariablesProperties PressAlt;
    private VariablesProperties CloudBase;
    private VariablesProperties DensAlt;
    private VariablesProperties VirtTemp;
    private VariablesProperties VaporPress;
    private VariablesProperties DailyRain;
    private VariablesProperties HourRain;
    private VariablesProperties HrRain24;
    private VariablesProperties RainRate;
    private VariablesProperties WindRun;
    private VariablesProperties DegHeat;
    private VariablesProperties DegCool;
    private VariablesProperties MoonPhase;
    private VariablesProperties MonthRain;
    private VariablesProperties DegHeatMo;
    private VariablesProperties DegCoolMo;
    private VariablesProperties WindRunMo;
    private VariablesProperties DegHeatYr;
    private VariablesProperties DegCoolYr;
    private VariablesProperties WindRunYr;
    private VariablesProperties time;
    private ArrayList<VariablesProperties> varProperties;

    /**
     * Constructor for all station properties
     */
    public DefaultStationVarProperties() {
        WindDir = new VariablesProperties("");
        WindSpd = new VariablesProperties("");
        WindGust = new VariablesProperties("");
        Humidity = new VariablesProperties("");
        TempIn = new VariablesProperties("");
        Temp = new VariablesProperties("");
        RawBarom = new VariablesProperties("");
        TotRain = new VariablesProperties("");
        ET = new VariablesProperties("");
        UV = new VariablesProperties("");
        Solar = new VariablesProperties("");
        WindChill = new VariablesProperties("");
        HeatIxIn = new VariablesProperties("");
        HeatIndex = new VariablesProperties("");
        DewPoint = new VariablesProperties("");
        BaromSL = new VariablesProperties("");
        PressAlt = new VariablesProperties("");
        CloudBase = new VariablesProperties("");
        DensAlt = new VariablesProperties("");
        VirtTemp = new VariablesProperties("");
        VaporPress = new VariablesProperties("");
        DailyRain = new VariablesProperties("");
        HourRain = new VariablesProperties("");
        HrRain24 = new VariablesProperties("");
        RainRate = new VariablesProperties("");
        WindRun = new VariablesProperties("");
        DegHeat = new VariablesProperties("");
        DegCool = new VariablesProperties("");
        MoonPhase = new VariablesProperties("");
        MonthRain = new VariablesProperties("");
        DegHeatMo = new VariablesProperties("");
        DegCoolMo = new VariablesProperties("");
        WindRunMo = new VariablesProperties("");
        DegHeatYr = new VariablesProperties("");
        DegCoolYr = new VariablesProperties("");
        WindRunYr = new VariablesProperties("");
        time = new VariablesProperties("");
        varProperties = new ArrayList<VariablesProperties>();
        setProperties();
        addAllProperties();

    }

    /**
     * Sets the properties to their defaults.
     */
    private void setProperties() {
        WindDir.setVariableKey("Wind Dir");
        WindDir.setDisplayName("Wind Dir");
        WindDir.setDescription("Wind Direction");
        WindDir.setUnits("°");
        WindDir.setMinUnit("0");
        WindDir.setMaxUnit("360");
        WindDir.setColor("Purple");
        WindDir.setType("INT");
        WindDir.setCommon("Wind");
        WindDir.setSpace("2");

        WindSpd.setVariableKey("Wind Spd");
        WindSpd.setDisplayName("Wind Spd");
        WindSpd.setDescription("Wind Speed");
        WindSpd.setUnits("mph");
        WindSpd.setMinUnit("0");
        WindSpd.setMaxUnit("80");
        WindSpd.setColor("Purple");
        WindSpd.setType("INT");
        WindSpd.setCommon("Wind");
        WindSpd.setSpace("5");

        WindGust.setVariableKey("Wind Gust");
        WindGust.setDisplayName("Wind Gust");
        WindGust.setDescription("Wind Gust");
        WindGust.setUnits("mph");
        WindGust.setMinUnit("0");
        WindGust.setMaxUnit("100");
        WindGust.setColor("Purple");
        WindGust.setType("INT");
        WindGust.setCommon("Wind");
        WindGust.setSpace("5");

        Humidity.setVariableKey("Humidity");
        Humidity.setDisplayName("Humidity");
        Humidity.setDescription("Humidity");
        Humidity.setUnits("%");
        Humidity.setMinUnit("0");
        Humidity.setMaxUnit("100");
        Humidity.setColor("Blue");
        Humidity.setType("FLOAT");
        Humidity.setCommon("Temperature");
        Humidity.setSpace("5");

        TempIn.setVariableKey("Temp In");
        TempIn.setDisplayName("Temp In");
        TempIn.setDescription("Inside Temperature");
        TempIn.setUnits("°F");
        TempIn.setMinUnit("0");
        TempIn.setMaxUnit("100");
        TempIn.setColor("Red");
        TempIn.setType("FLOAT");
        TempIn.setCommon("Temperature");
        TempIn.setSpace("5");

        Temp.setVariableKey("Temp");
        Temp.setDisplayName("Temp");
        Temp.setDescription("Outside Temperature");
        Temp.setUnits("°F");
        Temp.setMinUnit("0");
        Temp.setMaxUnit("100");
        Temp.setColor("Red");
        Temp.setType("FLOAT");
        Temp.setCommon("Temperature");
        Temp.setSpace("5");

        RawBarom.setVariableKey("Raw Barom");
        RawBarom.setDisplayName("Raw Barom");
        RawBarom.setDescription("Raw Barometer");
        RawBarom.setUnits("mb");
        RawBarom.setMinUnit("0");
        RawBarom.setMaxUnit("2000");
        RawBarom.setColor("Red");
        RawBarom.setType("FLOAT");
        RawBarom.setCommon("Pressure");
        RawBarom.setSpace("5");

        TotRain.setVariableKey("Tot Rain");
        TotRain.setDisplayName("Tot Rain");
        TotRain.setDescription("Total Rainfall");
        TotRain.setUnits("in");
        TotRain.setMinUnit("0");
        TotRain.setMaxUnit("10");
        TotRain.setColor("Blue");
        TotRain.setType("FLOAT");
        TotRain.setCommon("Rainfall");
        TotRain.setSpace("5");

        ET.setVariableKey("ET");
        ET.setDisplayName("ET");
        ET.setDescription("Evapotranspiration");
        ET.setUnits("in");
        ET.setMinUnit("0");
        ET.setMaxUnit("2000");
        ET.setColor("Blue");
        ET.setType("FLOAT");
        ET.setCommon("No Group");
        ET.setSpace("5");

        UV.setVariableKey("UV");
        UV.setDisplayName("Solar");
        UV.setDescription("Solar Radiation");
        UV.setUnits("Watts/Square Meter");
        UV.setMinUnit("0");
        UV.setMaxUnit("1350");
        UV.setColor("Dark Orange");
        UV.setType("FLOAT");
        UV.setCommon("Solar");
        UV.setSpace("1");

        Solar.setVariableKey("Solar");
        Solar.setDisplayName("Solar Wind");
        Solar.setDescription("Solar Wind");
        Solar.setUnits("W/sqm");
        Solar.setMinUnit("0");
        Solar.setMaxUnit("100");
        Solar.setColor("Dark Orange");
        Solar.setType("INT");
        Solar.setCommon("Solar");
        Solar.setSpace("5");

        WindChill.setVariableKey("Wind Chill");
        WindChill.setDisplayName("Wind Chill");
        WindChill.setDescription("Wind Chill");
        WindChill.setUnits("°F");
        WindChill.setMinUnit("-30");
        WindChill.setMaxUnit("80");
        WindChill.setColor("Red");
        WindChill.setType("FLOAT");
        WindChill.setCommon("Temperature");
        WindChill.setSpace("2");

        HeatIxIn.setVariableKey("Heat Index In");
        HeatIxIn.setDisplayName("Heat Index In");
        HeatIxIn.setDescription("Inside Heat Index");
        HeatIxIn.setUnits("°F");
        HeatIxIn.setMinUnit("0");
        HeatIxIn.setMaxUnit("100");
        HeatIxIn.setColor("Red");
        HeatIxIn.setType("FLOAT");
        HeatIxIn.setCommon("Heat Index");
        HeatIxIn.setSpace("5");

        HeatIndex.setVariableKey("Heat Index");
        HeatIndex.setDisplayName("Heat Index");
        HeatIndex.setDescription("Outside Heat Index");
        HeatIndex.setUnits("°F");
        HeatIndex.setMinUnit("0");
        HeatIndex.setMaxUnit("100");
        HeatIndex.setColor("Red");
        HeatIndex.setType("FLOAT");
        HeatIndex.setCommon("Heat Index");
        HeatIndex.setSpace("5");

        DewPoint.setVariableKey("Dew Point");
        DewPoint.setDisplayName("Dew Point");
        DewPoint.setDescription("Dew Point");
        DewPoint.setUnits("°F");
        DewPoint.setMinUnit("0");
        DewPoint.setMaxUnit("100");
        DewPoint.setColor("Green");
        DewPoint.setType("FLOAT");
        DewPoint.setCommon("Temperature");
        DewPoint.setSpace("5");

        BaromSL.setVariableKey("Barom SL");
        BaromSL.setDisplayName("Barom SL");
        BaromSL.setDescription("Barometer at Sea Level");
        BaromSL.setUnits("mb");
        BaromSL.setMinUnit("970");
        BaromSL.setMaxUnit("1050");
        BaromSL.setColor("Black");
        BaromSL.setType("FLOAT");
        BaromSL.setCommon("Pressure");
        BaromSL.setSpace("5");

        PressAlt.setVariableKey("Press Alt");
        PressAlt.setDisplayName("Press Alt");
        PressAlt.setDescription("Pressure Altitude");
        PressAlt.setUnits("ft");
        PressAlt.setMinUnit("0");
        PressAlt.setMaxUnit("2000");
        PressAlt.setColor("Black");
        PressAlt.setType("INT");
        PressAlt.setCommon("Pressure");
        PressAlt.setSpace("5");

        CloudBase.setVariableKey("Cloud Base");
        CloudBase.setDisplayName("Cloud Base");
        CloudBase.setDescription("Thousands of feet");
        CloudBase.setUnits("ft");
        CloudBase.setMinUnit("0");
        CloudBase.setMaxUnit("10000");
        CloudBase.setColor("Black");
        CloudBase.setType("INT");
        CloudBase.setCommon("No Group");
        CloudBase.setSpace("5");

        DensAlt.setVariableKey("Dens Alt");
        DensAlt.setDisplayName("Dens Alt");
        DensAlt.setDescription("Density Altitude");
        DensAlt.setUnits("ft");
        DensAlt.setMinUnit("0");
        DensAlt.setMaxUnit("2000");
        DensAlt.setColor("Black");
        DensAlt.setType("INT");
        DensAlt.setCommon("Pressure");
        DensAlt.setSpace("5");

        VirtTemp.setVariableKey("Virt Temp");
        VirtTemp.setDisplayName("Virt Temp");
        VirtTemp.setDescription("Virtual Temperature");
        VirtTemp.setUnits("°F");
        VirtTemp.setMinUnit("0");
        VirtTemp.setMaxUnit("100");
        VirtTemp.setColor("Red");
        VirtTemp.setType("FLOAT");
        VirtTemp.setCommon("No Group");
        VirtTemp.setSpace("5");

        VaporPress.setVariableKey("Vapor Press");
        VaporPress.setDisplayName("Vapor Press");
        VaporPress.setDescription("Vapor Pressure");
        VaporPress.setUnits("mb");
        VaporPress.setMinUnit("0");
        VaporPress.setMaxUnit("15");
        VaporPress.setColor("Black");
        VaporPress.setType("FLOAT");
        VaporPress.setCommon("Pressure");
        VaporPress.setSpace("3");

        DailyRain.setVariableKey("DailyRain");
        DailyRain.setDisplayName("DailyRain");
        DailyRain.setDescription("Daily Rainfall");
        DailyRain.setUnits("in");
        DailyRain.setMinUnit("0");
        DailyRain.setMaxUnit("3");
        DailyRain.setColor("Aqua");
        DailyRain.setType("FLOAT");
        DailyRain.setCommon("Rainfall");
        DailyRain.setSpace("5");

        HourRain.setVariableKey("HourRain");
        HourRain.setDisplayName("HourRain");
        HourRain.setDescription("Hourly Rainfall");
        HourRain.setUnits("in");
        HourRain.setMinUnit("0");
        HourRain.setMaxUnit("10");
        HourRain.setColor("Aqua");
        HourRain.setType("FLOAT");
        HourRain.setCommon("Rainfall");
        HourRain.setSpace("5");

        HrRain24.setVariableKey("24HrRain");
        HrRain24.setDisplayName("24HrRain");
        HrRain24.setDescription("24 Hour Rainfall");
        HrRain24.setUnits("in");
        HrRain24.setMinUnit("0");
        HrRain24.setMaxUnit("10");
        HrRain24.setColor("Aqua");
        HrRain24.setType("FLOAT");
        HrRain24.setCommon("Rainfall");
        HrRain24.setSpace("5");

        RainRate.setVariableKey("RainRate");
        RainRate.setDisplayName("RainRate");
        RainRate.setDescription("Rainfall Rate");
        RainRate.setUnits("in/hr");
        RainRate.setMinUnit("0");
        RainRate.setMaxUnit("3");
        RainRate.setColor("Aqua");
        RainRate.setType("FLOAT");
        RainRate.setCommon("Rainfall");
        RainRate.setSpace("5");

        WindRun.setVariableKey("Wind Run");
        WindRun.setDisplayName("Wind Run");
        WindRun.setDescription("Wind Run");
        WindRun.setUnits("miles");
        WindRun.setMinUnit("0");
        WindRun.setMaxUnit("100");
        WindRun.setColor("Purple");
        WindRun.setType("INT");
        WindRun.setCommon("No Group");
        WindRun.setSpace("5");

        DegHeat.setVariableKey("Deg Heat");
        DegHeat.setDisplayName("Deg Heat");
        DegHeat.setDescription("Degrees Heating");
        DegHeat.setUnits("°F");
        DegHeat.setMinUnit("0");
        DegHeat.setMaxUnit("100");
        DegHeat.setColor("Black");
        DegHeat.setType("FLOAT");
        DegHeat.setCommon("Heat Index");
        DegHeat.setSpace("5");

        DegCool.setVariableKey("Deg Cool");
        DegCool.setDisplayName("Deg Cool");
        DegCool.setDescription("Degrees Cooling");
        DegCool.setUnits("°F");
        DegCool.setMinUnit("0");
        DegCool.setMaxUnit("100");
        DegCool.setColor("Black");
        DegCool.setType("FLOAT");
        DegCool.setCommon("Heat Index");
        DegCool.setSpace("5");

        MoonPhase.setVariableKey("Moon Phase");
        MoonPhase.setDisplayName("Moon Phase");
        MoonPhase.setDescription("Moon Phase");
        MoonPhase.setUnits("");
        MoonPhase.setMinUnit("0");
        MoonPhase.setMaxUnit("29.5");
        MoonPhase.setColor("Orange");
        MoonPhase.setType("FLOAT");
        MoonPhase.setCommon("Solar");
        MoonPhase.setSpace("5");

        MonthRain.setVariableKey("MonthRain");
        MonthRain.setDisplayName("MonthRain");
        MonthRain.setDescription("Monthly Rainfall");
        MonthRain.setUnits("in");
        MonthRain.setMinUnit("0");
        MonthRain.setMaxUnit("10");
        MonthRain.setColor("Blue");
        MonthRain.setType("FLOAT");
        MonthRain.setCommon("Monthly");
        MonthRain.setSpace("5");

        DegHeatMo.setVariableKey("DegHeat Mo");
        DegHeatMo.setDisplayName("DegHeat Mo");
        DegHeatMo.setDescription("Monthly Degrees Heating");
        DegHeatMo.setUnits("°F");
        DegHeatMo.setMinUnit("0");
        DegHeatMo.setMaxUnit("10");
        DegHeatMo.setColor("Red");
        DegHeatMo.setType("FLOAT");
        DegHeatMo.setCommon("Monthly");
        DegHeatMo.setSpace("5");

        DegCoolMo.setVariableKey("DegCool Mo");
        DegCoolMo.setDisplayName("DegCool Mo");
        DegCoolMo.setDescription("Monthly Degrees Cooling");
        DegCoolMo.setUnits("°F");
        DegCoolMo.setMinUnit("0");
        DegCoolMo.setMaxUnit("10");
        DegCoolMo.setColor("Blue");
        DegCoolMo.setType("FLOAT");
        DegCoolMo.setCommon("Monthly");
        DegCoolMo.setSpace("5");

        WindRunMo.setVariableKey("WindRun Mo");
        WindRunMo.setDisplayName("WindRun Mo");
        WindRunMo.setDescription("Monthly Wind Run");
        WindRunMo.setUnits("miles");
        WindRunMo.setMinUnit("0");
        WindRunMo.setMaxUnit("50");
        WindRunMo.setColor("Purple");
        WindRunMo.setType("INT");
        WindRunMo.setCommon("Monthly");
        WindRunMo.setSpace("5");

        DegHeatYr.setVariableKey("DegHeat Yr");
        DegHeatYr.setDisplayName("DegHeat Yr");
        DegHeatYr.setDescription("Yearly Degrees Heating");
        DegHeatYr.setUnits("°F");
        DegHeatYr.setMinUnit("0");
        DegHeatYr.setMaxUnit("100");
        DegHeatYr.setColor("Red");
        DegHeatYr.setType("FLOAT");
        DegHeatYr.setCommon("Yearly");
        DegHeatYr.setSpace("5");

        DegCoolYr.setVariableKey("DegCool Yr");
        DegCoolYr.setDisplayName("DegCool Yr");
        DegCoolYr.setDescription("Yearly Degrees Cooling");
        DegCoolYr.setUnits("°F");
        DegCoolYr.setMinUnit("0");
        DegCoolYr.setMaxUnit("100");
        DegCoolYr.setColor("Blue");
        DegCoolYr.setType("FLOAT");
        DegCoolYr.setCommon("Yearly");
        DegCoolYr.setSpace("5");

        WindRunYr.setVariableKey("WindRun Yr");
        WindRunYr.setDisplayName("WindRun Yr");
        WindRunYr.setDescription("Yearly Wind Run");
        WindRunYr.setUnits("miles");
        WindRunYr.setMinUnit("0");
        WindRunYr.setMaxUnit("100");
        WindRunYr.setColor("Green");
        WindRunYr.setType("INT");
        WindRunYr.setCommon("Yearly");
        WindRunYr.setSpace("5");

        time.setVariableKey("Time");
        time.setDisplayName("Time");
        time.setDescription("Time");
        time.setUnits("minutes");
        time.setMinUnit("0");
        time.setMaxUnit("2400");
        time.setColor("Black");
        time.setType("TIME");
    }

    /**
     * Adds all properties to an array list of properties.
     */
    private void addAllProperties() {
        varProperties.add(WindDir);
        varProperties.add(WindSpd);
        varProperties.add(WindGust);
        varProperties.add(Humidity);
        varProperties.add(TempIn);
        varProperties.add(Temp);
        varProperties.add(RawBarom);
        varProperties.add(TotRain);
        varProperties.add(ET);
        varProperties.add(UV);
        varProperties.add(Solar);
        varProperties.add(WindChill);
        varProperties.add(HeatIxIn);
        varProperties.add(HeatIndex);
        varProperties.add(DewPoint);
        varProperties.add(BaromSL);
        varProperties.add(PressAlt);
        varProperties.add(CloudBase);
        varProperties.add(DensAlt);
        varProperties.add(VirtTemp);
        varProperties.add(VaporPress);
        varProperties.add(DailyRain);
        varProperties.add(HourRain);
        varProperties.add(HrRain24);
        varProperties.add(RainRate);
        varProperties.add(WindRun);
        varProperties.add(DegHeat);
        varProperties.add(DegCool);
        varProperties.add(MoonPhase);
        varProperties.add(MonthRain);
        varProperties.add(DegHeatMo);
        varProperties.add(DegCoolMo);
        varProperties.add(WindRunMo);
        varProperties.add(DegHeatYr);
        varProperties.add(DegCoolYr);
        varProperties.add(WindRunYr);
        varProperties.add(time);
    }

    /**
     * Returns the number of total station properties.
     * @return The number of total station properties.
     */
    public int size() {
        return varProperties.size();
    }

    /**
     * Returns all properties.
     * @return The total station properties.
     */
    public ArrayList<VariablesProperties> getAllproperties() {
        return varProperties;
    }

    /**
     * Returns the variable with the given a station variable name.
     * @param name The station variable's name.
     * @return The variableProperties.
     */
    public VariablesProperties getVariable(String name) {

        for (int i = 0; i < varProperties.size(); ++i) {
            if (name.equals(varProperties.get(i).getDescription())) {
                return varProperties.get(i);
            }
        }
        return null;
    }
}

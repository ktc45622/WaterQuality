package weather.common.data.resource;

import java.io.Serializable;
import java.util.TimeZone;

/**
 * Enumeration that specifies in which time zone the resources resides.
 *
 * NOTE: The enumeration names must match up with the timeZone entry in the table
 *       time_zone_information of the database.
 * NOTE: Do NOT change these string values or overload the toString method,
 *       otherwise the time zone will be set incorrectly.
 * NOTE: Java displays all of the UTC options as "UTC."  To display as "UTC,"
 *       one can use <code>String.replaceAll(...)</code>.
 *
 * @author Eric Subach (2010)
 * @version Spring 2010
 */
public enum ResourceTimeZone implements Serializable {
    UTC       ("UTC", "GMT"),
    
    US_PT   ("US Pacific (with DST)", "America/Los_Angeles"),
    US_MT   ("US Mountain (with DST)", "America/Denver"),
    US_CT   ("US Central (with DST)", "America/Chicago"),
    US_ET   ("US Eastern (with DST)", "America/New_York"),
    US_AKT  ("US Alaskan (with DST)", "America/Anchorage"),
    US_HT   ("US Hawaii Aleutian (with DST)", "America/Adak"),
    US_AST  ("US Atlantic (no DST)", "America/Puerto_Rico"),
    US_MST  ("US Mountain (no DST)", "America/Phoenix"),
    US_HST  ("US Hawaii Aleutian (no DST)", "Pacific/Honolulu"),
    US_SST  ("US Samoa (no DST)", "Pacific/Samoa"),
    US_ChST ("US Chamorro (no DST)", "Pacific/Guam"),

    UTC_Minus_12_00 ("UTC-12:00", "GMT-12:00"),
    UTC_Minus_11_00 ("UTC-11:00", "GMT-11:00"),
    UTC_Minus_10_00 ("UTC-10:00", "GMT-10:00"),
    UTC_Minus_09_30 ("UTC-09:30", "GMT-09:30"),
    UTC_Minus_09_00 ("UTC-09:00", "GMT-09:00"),
    UTC_Minus_08_00 ("UTC-08:00", "GMT-08:00"),
    UTC_Minus_07_00 ("UTC-07:00", "GMT-07:00"),
    UTC_Minus_06_00 ("UTC-06:00", "GMT-06:00"),
    UTC_Minus_05_00 ("UTC-05:00", "GMT-05:00"),
    UTC_Minus_04_30 ("UTC-04:30", "GMT-04:30"),
    UTC_Minus_04_00 ("UTC-04:00", "GMT-04:00"),
    UTC_Minus_03_30 ("UTC-03:30", "GMT-03:30"),
    UTC_Minus_03_00 ("UTC-03:00", "GMT-03:00"),
    UTC_Minus_02_30 ("UTC-02:30", "GMT-02:30"),
    UTC_Minus_02_00 ("UTC-02:00", "GMT-02:00"),
    UTC_Minus_01_00 ("UTC-01:00", "GMT-01:00"),
    UTC_00_00       ("UTC 00:00", "GMT"),
    UTC_Plus_01_00 ("UTC+01:00", "GMT+01:00"),
    UTC_Plus_02_00 ("UTC+02:00", "GMT+02:00"),
    UTC_Plus_03_00 ("UTC+03:00", "GMT+03:00"),
    UTC_Plus_03_30 ("UTC+03:30", "GMT+03:30"),
    UTC_Plus_04_00 ("UTC+04:00", "GMT+04:00"),
    UTC_Plus_04_30 ("UTC+04:30", "GMT+04:30"),
    UTC_Plus_05_00 ("UTC+05:00", "GMT+05:00"),
    UTC_Plus_05_30 ("UTC+05:30", "GMT+05:30"),
    UTC_Plus_05_45 ("UTC+05:45", "GMT+05:45"),
    UTC_Plus_06_00 ("UTC+06:00", "GMT+06:00"),
    UTC_Plus_06_30 ("UTC+06:30", "GMT+06:30"),
    UTC_Plus_07_00 ("UTC+07:00", "GMT+07:00"),
    UTC_Plus_08_00 ("UTC+08:00", "GMT+08:00"),
    UTC_Plus_08_45 ("UTC+08:45", "GMT+08:45"),
    UTC_Plus_09_00 ("UTC+09:00", "GMT+09:00"),
    UTC_Plus_09_30 ("UTC+09:30", "GMT+09:30"),
    UTC_Plus_09_45 ("UTC+09:45", "GMT+09:45"),
    UTC_Plus_10_00 ("UTC+10:00", "GMT+10:00"),
    UTC_Plus_10_30 ("UTC+10:30", "GMT+10:30"),
    UTC_Plus_11_00 ("UTC+11:00", "GMT+11:00"),
    UTC_Plus_11_30 ("UTC+11:30", "GMT+11:30"),
    UTC_Plus_12_00 ("UTC+12:00", "GMT+12:00"),
    UTC_Plus_12_45 ("UTC+12:45", "GMT+12:45"),
    UTC_Plus_13_00 ("UTC+13:00", "GMT+13:00"),
    UTC_Plus_13_45 ("UTC+13:45", "GMT+13:45"),
    UTC_Plus_14_00 ("UTC+14:00", "GMT+14:00");


    private String stringValue;
    private TimeZone timeZone;

    /**
     * Determines if a de-serialized file is compatible with this class.
     *
     * Maintainers must change this value if and only if the new version
     * of this class is not compatible with old versions. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html> details. </a>
     *
     * Not necessary to include in first version of the class, but
     * included here as a reminder of its importance.
     */
    private static final long serialVersionUID = 1L;


    ResourceTimeZone (String displayName, String javaTimeZonaID) {
        this.stringValue = displayName;
        this.timeZone = TimeZone.getTimeZone(javaTimeZonaID);
    }

    /**
     * Gets a string representation of time zone for displaying on screen.
     *
     * NOTE: To get a string for updating the database, use the toString method.
     *
     * @return A string representing the time zone.
     */
    public String displayString () {
        return (stringValue);
    }


    /**
     * Gets a SimpleTimeZone object.
     *
     * @return A SimpleTimeZone object.
     */
    public TimeZone getTimeZone () {
        return (timeZone);
    }

    /**
     * Return the ResourceTimeZone with the corresponding string value.
     *
     * @param value A string value of the enumeration.
     * 
     * @return An enumeration with that string value.
     */
    public static ResourceTimeZone getEnum (String value) {
        ResourceTimeZone timeZone = null;

        for (ResourceTimeZone r : ResourceTimeZone.values ()) {
            if (r.stringValue.equals (value)) {
                timeZone = r;
                break;
            }
        }

        return (timeZone);
    }
    
    /**
     * Searches the values of <code>ResourceTimeZone</code> to find one that
     * matches the local time zone.  If one is found, its display string is
     * returned.  Otherwise, the UTC display name is returned.
     * @return The display string of the local time zone or the UTC display name
     * if the local time zone is not found.
     */
    public static String getLocalDisplayString() {
        //Assume UTC in case local time zone is not in values list.
        String localDisplayString = UTC.displayString();
        
        for (ResourceTimeZone r : ResourceTimeZone.values ()) {
            if (r.timeZone.equals (TimeZone.getDefault())) {
                localDisplayString = r.displayString();
                break;
            }
        }
        
        return localDisplayString;
        
    }
}

/*
 * Java API list of time zone IDs.

Etc/GMT+12
Etc/GMT+11
MIT
Pacific/Apia
Pacific/Midway
Pacific/Niue
Pacific/Pago_Pago
Pacific/Samoa
US/Samoa
America/Adak
America/Atka
Etc/GMT+10
HST
Pacific/Fakaofo
Pacific/Honolulu
Pacific/Johnston
Pacific/Rarotonga
Pacific/Tahiti
SystemV/HST10
US/Aleutian
US/Hawaii
Pacific/Marquesas
AST
America/Anchorage
America/Juneau
America/Nome
America/Yakutat
Etc/GMT+9
Pacific/Gambier
SystemV/YST9
SystemV/YST9YDT
US/Alaska
America/Dawson
America/Ensenada
America/Los_Angeles
America/Tijuana
America/Vancouver
America/Whitehorse
Canada/Pacific
Canada/Yukon
Etc/GMT+8
Mexico/BajaNorte
PST
PST8PDT
Pacific/Pitcairn
SystemV/PST8
SystemV/PST8PDT
US/Pacific
US/Pacific-New
America/Boise
America/Cambridge_Bay
America/Chihuahua
America/Dawson_Creek
America/Denver
America/Edmonton
America/Hermosillo
America/Inuvik
America/Mazatlan
America/Phoenix
America/Shiprock
America/Yellowknife
Canada/Mountain
Etc/GMT+7
MST
MST7MDT
Mexico/BajaSur
Navajo
PNT
SystemV/MST7
SystemV/MST7MDT
US/Arizona
US/Mountain
America/Belize
America/Cancun
America/Chicago
America/Costa_Rica
America/El_Salvador
America/Guatemala
America/Indiana/Knox
America/Indiana/Tell_City
America/Knox_IN
America/Managua
America/Menominee
America/Merida
America/Mexico_City
America/Monterrey
America/North_Dakota/Center
America/North_Dakota/New_Salem
America/Rainy_River
America/Rankin_Inlet
America/Regina
America/Swift_Current
America/Tegucigalpa
America/Winnipeg
CST
CST6CDT
Canada/Central
Canada/East-Saskatchewan
Canada/Saskatchewan
Chile/EasterIsland
Etc/GMT+6
Mexico/General
Pacific/Easter
Pacific/Galapagos
SystemV/CST6
SystemV/CST6CDT
US/Central
US/Indiana-Starke
America/Atikokan
America/Bogota
America/Cayman
America/Coral_Harbour
America/Detroit
America/Fort_Wayne
America/Grand_Turk
America/Guayaquil
America/Havana
America/Indiana/Indianapolis
America/Indiana/Marengo
America/Indiana/Petersburg
America/Indiana/Vevay
America/Indiana/Vincennes
America/Indiana/Winamac
America/Indianapolis
America/Iqaluit
America/Jamaica
America/Kentucky/Louisville
America/Kentucky/Monticello
America/Lima
America/Louisville
America/Montreal
America/Nassau
America/New_York
America/Nipigon
America/Panama
America/Pangnirtung
America/Port-au-Prince
America/Resolute
America/Thunder_Bay
America/Toronto
Canada/Eastern
Cuba
EST
EST5EDT
Etc/GMT+5
IET
Jamaica
SystemV/EST5
SystemV/EST5EDT
US/East-Indiana
US/Eastern
US/Michigan
America/Caracas
America/Anguilla
America/Antigua
America/Argentina/San_Luis
America/Aruba
America/Asuncion
America/Barbados
America/Blanc-Sablon
America/Boa_Vista
America/Campo_Grande
America/Cuiaba
America/Curacao
America/Dominica
America/Eirunepe
America/Glace_Bay
America/Goose_Bay
America/Grenada
America/Guadeloupe
America/Guyana
America/Halifax
America/La_Paz
America/Manaus
America/Marigot
America/Martinique
America/Moncton
America/Montserrat
America/Port_of_Spain
America/Porto_Acre
America/Porto_Velho
America/Puerto_Rico
America/Rio_Branco
America/Santiago
America/Santo_Domingo
America/St_Barthelemy
America/St_Kitts
America/St_Lucia
America/St_Thomas
America/St_Vincent
America/Thule
America/Tortola
America/Virgin
Antarctica/Palmer
Atlantic/Bermuda
Atlantic/Stanley
Brazil/Acre
Brazil/West
Canada/Atlantic
Chile/Continental
Etc/GMT+4
PRT
SystemV/AST4
SystemV/AST4ADT
America/St_Johns
CNT
Canada/Newfoundland
AGT
America/Araguaina
America/Argentina/Buenos_Aires
America/Argentina/Catamarca
America/Argentina/ComodRivadavia
America/Argentina/Cordoba
America/Argentina/Jujuy
America/Argentina/La_Rioja
America/Argentina/Mendoza
America/Argentina/Rio_Gallegos
America/Argentina/Salta
America/Argentina/San_Juan
America/Argentina/Tucuman
America/Argentina/Ushuaia
America/Bahia
America/Belem
America/Buenos_Aires
America/Catamarca
America/Cayenne
America/Cordoba
America/Fortaleza
America/Godthab
America/Jujuy
America/Maceio
America/Mendoza
America/Miquelon
America/Montevideo
America/Paramaribo
America/Recife
America/Rosario
America/Santarem
America/Sao_Paulo
Antarctica/Rothera
BET
Brazil/East
Etc/GMT+3
America/Noronha
Atlantic/South_Georgia
Brazil/DeNoronha
Etc/GMT+2
America/Scoresbysund
Atlantic/Azores
Atlantic/Cape_Verde
Etc/GMT+1
Africa/Abidjan
Africa/Accra
Africa/Bamako
Africa/Banjul
Africa/Bissau
Africa/Casablanca
Africa/Conakry
Africa/Dakar
Africa/El_Aaiun
Africa/Freetown
Africa/Lome
Africa/Monrovia
Africa/Nouakchott
Africa/Ouagadougou
Africa/Sao_Tome
Africa/Timbuktu
America/Danmarkshavn
Atlantic/Canary
Atlantic/Faeroe
Atlantic/Faroe
Atlantic/Madeira
Atlantic/Reykjavik
Atlantic/St_Helena
Eire
Etc/GMT
Etc/GMT+0
Etc/GMT-0
Etc/GMT0
Etc/Greenwich
Etc/UCT
Etc/UTC
Etc/Universal
Etc/Zulu
Europe/Belfast
Europe/Dublin
Europe/Guernsey
Europe/Isle_of_Man
Europe/Jersey
Europe/Lisbon
Europe/London
GB
GB-Eire
UTC
GMT0
Greenwich
Iceland
Portugal
UCT
UTC
Universal
WET
Zulu
Africa/Algiers
Africa/Bangui
Africa/Brazzaville
Africa/Ceuta
Africa/Douala
Africa/Kinshasa
Africa/Lagos
Africa/Libreville
Africa/Luanda
Africa/Malabo
Africa/Ndjamena
Africa/Niamey
Africa/Porto-Novo
Africa/Tunis
Africa/Windhoek
Arctic/Longyearbyen
Atlantic/Jan_Mayen
CET
ECT
Etc/GMT-1
Europe/Amsterdam
Europe/Andorra
Europe/Belgrade
Europe/Berlin
Europe/Bratislava
Europe/Brussels
Europe/Budapest
Europe/Copenhagen
Europe/Gibraltar
Europe/Ljubljana
Europe/Luxembourg
Europe/Madrid
Europe/Malta
Europe/Monaco
Europe/Oslo
Europe/Paris
Europe/Podgorica
Europe/Prague
Europe/Rome
Europe/San_Marino
Europe/Sarajevo
Europe/Skopje
Europe/Stockholm
Europe/Tirane
Europe/Vaduz
Europe/Vatican
Europe/Vienna
Europe/Warsaw
Europe/Zagreb
Europe/Zurich
MET
Poland
ART
Africa/Blantyre
Africa/Bujumbura
Africa/Cairo
Africa/Gaborone
Africa/Harare
Africa/Johannesburg
Africa/Kigali
Africa/Lubumbashi
Africa/Lusaka
Africa/Maputo
Africa/Maseru
Africa/Mbabane
Africa/Tripoli
Asia/Amman
Asia/Beirut
Asia/Damascus
Asia/Gaza
Asia/Istanbul
Asia/Jerusalem
Asia/Nicosia
Asia/Tel_Aviv
CAT
EET
Egypt
Etc/GMT-2
Europe/Athens
Europe/Bucharest
Europe/Chisinau
Europe/Helsinki
Europe/Istanbul
Europe/Kaliningrad
Europe/Kiev
Europe/Mariehamn
Europe/Minsk
Europe/Nicosia
Europe/Riga
Europe/Simferopol
Europe/Sofia
Europe/Tallinn
Europe/Tiraspol
Europe/Uzhgorod
Europe/Vilnius
Europe/Zaporozhye
Israel
Libya
Turkey
Africa/Addis_Ababa
Africa/Asmara
Africa/Asmera
Africa/Dar_es_Salaam
Africa/Djibouti
Africa/Kampala
Africa/Khartoum
Africa/Mogadishu
Africa/Nairobi
Antarctica/Syowa
Asia/Aden
Asia/Baghdad
Asia/Bahrain
Asia/Kuwait
Asia/Qatar
Asia/Riyadh
EAT
Etc/GMT-3
Europe/Moscow
Europe/Volgograd
Indian/Antananarivo
Indian/Comoro
Indian/Mayotte
W-SU
Asia/Riyadh87
Asia/Riyadh88
Asia/Riyadh89
Mideast/Riyadh87
Mideast/Riyadh88
Mideast/Riyadh89
Asia/Tehran
Iran
Asia/Baku
Asia/Dubai
Asia/Muscat
Asia/Tbilisi
Asia/Yerevan
Etc/GMT-4
Europe/Samara
Indian/Mahe
Indian/Mauritius
Indian/Reunion
NET
Asia/Kabul
Asia/Aqtau
Asia/Aqtobe
Asia/Ashgabat
Asia/Ashkhabad
Asia/Dushanbe
Asia/Karachi
Asia/Oral
Asia/Samarkand
Asia/Tashkent
Asia/Yekaterinburg
Etc/GMT-5
Indian/Kerguelen
Indian/Maldives
PLT
Asia/Calcutta
Asia/Colombo
Asia/Kolkata
IST
Asia/Kathmandu
Asia/Katmandu
Antarctica/Mawson
Antarctica/Vostok
Asia/Almaty
Asia/Bishkek
Asia/Dacca
Asia/Dhaka
Asia/Novosibirsk
Asia/Omsk
Asia/Qyzylorda
Asia/Thimbu
Asia/Thimphu
BST
Etc/GMT-6
Indian/Chagos
Asia/Rangoon
Indian/Cocos
Antarctica/Davis
Asia/Bangkok
Asia/Ho_Chi_Minh
Asia/Hovd
Asia/Jakarta
Asia/Krasnoyarsk
Asia/Phnom_Penh
Asia/Pontianak
Asia/Saigon
Asia/Vientiane
Etc/GMT-7
Indian/Christmas
VST
Antarctica/Casey
Asia/Brunei
Asia/Choibalsan
Asia/Chongqing
Asia/Chungking
Asia/Harbin
Asia/Hong_Kong
Asia/Irkutsk
Asia/Kashgar
Asia/Kuala_Lumpur
Asia/Kuching
Asia/Macao
Asia/Macau
Asia/Makassar
Asia/Manila
Asia/Shanghai
Asia/Singapore
Asia/Taipei
Asia/Ujung_Pandang
Asia/Ulaanbaatar
Asia/Ulan_Bator
Asia/Urumqi
Australia/Perth
Australia/West
CTT
Etc/GMT-8
Hongkong
PRC
Singapore
Australia/Eucla
Asia/Dili
Asia/Jayapura
Asia/Pyongyang
Asia/Seoul
Asia/Tokyo
Asia/Yakutsk
Etc/GMT-9
JST
Japan
Pacific/Palau
ROK
ACT
Australia/Adelaide
Australia/Broken_Hill
Australia/Darwin
Australia/North
Australia/South
Australia/Yancowinna
AET
Antarctica/DumontDUrville
Asia/Sakhalin
Asia/Vladivostok
Australia/ACT
Australia/Brisbane
Australia/Canberra
Australia/Currie
Australia/Hobart
Australia/Lindeman
Australia/Melbourne
Australia/NSW
Australia/Queensland
Australia/Sydney
Australia/Tasmania
Australia/Victoria
Etc/GMT-10
Pacific/Guam
Pacific/Port_Moresby
Pacific/Saipan
Pacific/Truk
Pacific/Yap
Australia/LHI
Australia/Lord_Howe
Asia/Magadan
Etc/GMT-11
Pacific/Efate
Pacific/Guadalcanal
Pacific/Kosrae
Pacific/Noumea
Pacific/Ponape
SST
Pacific/Norfolk
Antarctica/McMurdo
Antarctica/South_Pole
Asia/Anadyr
Asia/Kamchatka
Etc/GMT-12
Kwajalein
NST
NZ
Pacific/Auckland
Pacific/Fiji
Pacific/Funafuti
Pacific/Kwajalein
Pacific/Majuro
Pacific/Nauru
Pacific/Tarawa
Pacific/Wake
Pacific/Wallis
NZ-CHAT
Pacific/Chatham
Etc/GMT-13
Pacific/Enderbury
Pacific/Tongatapu
Etc/GMT-14
Pacific/Kiritimati
 */

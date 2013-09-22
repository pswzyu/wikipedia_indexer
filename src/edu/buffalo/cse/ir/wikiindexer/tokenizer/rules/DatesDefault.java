package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.DATES)
public class DatesDefault {

	public DatesDefault() {
		
	}
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream == null)
			return;
		stream.reset();
		boolean isBC = false,
				isAD = false,
				containDate = false,
				containTime = false,
				containPM = false;
		String year = "1900",
				month = null,
				day = null,
				hour = "00",
				minute = "00",
				second = "00";
		int tokenCount = 0;
		while (stream.hasNext()) {
			String token = stream.next();
			token = token.toLowerCase();
			switch (token) {
			case "jan":
			case "january": tokenCount += 1; containDate = true; month = "01"; break;
			case "feb":
			case "february": tokenCount += 1; month = "02"; break;
			case "mar":
			case "march": tokenCount += 1; month = "03"; break;
			case "apr":
			case "april": tokenCount += 1; month = "04"; break;
			case "may": tokenCount += 1; month = "05"; break;
			case "jun":
			case "june": tokenCount += 1; month = "06"; break;
			case "jul":
			case "july": tokenCount += 1; month = "07"; break;
			case "aug":
			case "august": tokenCount += 1; month = "08"; break;
			case "sep":
			case "september": tokenCount += 1; month = "09"; break;
			case "oct":
			case "october": tokenCount += 1; month = "10"; break;
			case "nov":
			case "november": tokenCount += 1; month = "11"; break;
			case "dec":
			case "december": tokenCount += 1; month = "12"; break;
			case "monday":
			case "tuesday":
			case "wednesday":
			case "thursday":
			case "friday":
			case "saturday":
			case "sunday":
			case "on":
			case "utc": tokenCount += 1; break;
			case "bc":
			case "bc.": tokenCount +=1; isBC = true; break;
			case "ad":
			case "ad.": tokenCount +=1; isAD = true; break;
			default:
				if (token.matches("\\d{1,2}(:\\d{1,2}){1,2}\\s*[aApPmM\\.]*")) {
					int indexOfColon = token.indexOf(':');
					int lastIndexOfColon = token.lastIndexOf(':');
					hour = token.substring(0, indexOfColon);
					minute = token.substring(indexOfColon + 1, indexOfColon + 3);
					if (lastIndexOfColon > indexOfColon) {
						second = token.substring(lastIndexOfColon + 1, lastIndexOfColon + 3);
					}
					if(token.contains("pm")) {
						containPM = true;
					}
				} else if (token.matches("\\d{1,4}")) {
					int length = token.length();
					containDate = true;
					if (length == 4) {
						year = token;
					} else if (length == 3) {
						year = "0" + token;
					} else if (length == 2) {
						if (isAD || isBC) {
							if (year.equals(null)) {
								year = "00" + token;
							}
						} else {
							day = token;
						}
					} else {
						day = "0" + token;
					}
				} else if (true) {
					if (tokenCount > 0) {
						String temporal = "";
						while (tokenCount > 0) {
							tokenCount -= 1;
							stream.remove();
						}
						if (containDate) {
							if (month.equals(null)) {
								month = "01";
							}
							if (day.equals(null)) {
								day = "01";
							}
							if (isBC) {
								temporal += "-";
							}
							temporal += year + month + day;
						}
						if (containTime) {
							if (temporal.length() > 2) {
								temporal += " ";
							}
							if (containPM) {
								Integer tmpHour = Integer.valueOf(hour);
								tmpHour += 12;
								hour = tmpHour.toString();
							}
							if(hour.length() == 1) {
								hour = "0" + hour;
							}
							temporal += hour + ":" + minute + ":" + second;
						}
						stream.set(temporal);
					}
				}
			}
		}
	}

}

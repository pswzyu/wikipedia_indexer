package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.DATES)
public class DatesDefault implements TokenizerRule {
	private boolean isBC = false,
			isAD = false,
			containDate = false,
			containTime = false,
			containPM = false;
	private String year = null,
			month = null,
			day = null,
			hour = null,
			minute = null,
			second = null;
	private int tokenCount = 0;
	
	public void apply(TokenStream stream) throws TokenizerException {
		if (stream == null)
			return;
		stream.reset();
		Pattern patternTime = Pattern.compile("\\d{1,2}(:\\d{1,2}){1,2}\\s*[aApPmM\\.,]*");
		Pattern patternNumber = Pattern.compile("\\d{1,4}[,\\.]*");
		Pattern patternTwoYearPattern = Pattern.compile("\\d{4}.\\d{1,4}[,\\.]*");
		while (stream.hasNext()) {
			String token = stream.next();
			token = token.toLowerCase();
			switch (token) {
			case "jan":
			case "january":
				if(this.setMonth("01")) {
					tokenCount += 1; break;
				} else {
					this.processTemporal(stream);
					break;
				}
			case "feb":
			case "february":
				if(this.setMonth("02")) {
					tokenCount += 1; break;
				} else {
					this.processTemporal(stream);
					break;
				}
			case "mar":
			case "march":
				if(this.setMonth("03")) {
					tokenCount += 1; break;
				} else {
					this.processTemporal(stream);
					break;
				}
			case "apr":
			case "april":
				if(this.setMonth("04")) {
					tokenCount += 1; break;
				} else {
					this.processTemporal(stream);
					break;
				}
			case "may":
				if(this.setMonth("05")) {
					tokenCount += 1; break;
				} else {
					this.processTemporal(stream);
					break;
				}
			case "jun":
			case "june":
				if(this.setMonth("06")) {
					tokenCount += 1; break;
				} else {
					this.processTemporal(stream);
					break;
				}
			case "jul":
			case "july":
				if(this.setMonth("07")) {
					tokenCount += 1; break;
				} else {
					this.processTemporal(stream);
					break;
				}
			case "aug":
			case "august":
				if(this.setMonth("08")) {
					tokenCount += 1; break;
				} else {
					this.processTemporal(stream);
					break;
				}
			case "sep":
			case "september":
				if(this.setMonth("09")) {
					tokenCount += 1; break;
				} else {
					this.processTemporal(stream);
					break;
				}
			case "oct":
			case "october":
				if(this.setMonth("10")) {
					tokenCount += 1; break;
				} else {
					this.processTemporal(stream);
					break;
				}
			case "nov":
			case "november":
				if(this.setMonth("11")) {
					tokenCount += 1; break;
				} else {
					this.processTemporal(stream);
					break;
				}
			case "dec":
			case "december":
				if(this.setMonth("12")) {
					tokenCount += 1; break;
				} else {
					this.processTemporal(stream);
					break;
				}
			case "monday":
			case "tuesday":
			case "wednesday":
			case "thursday":
			case "friday":
			case "saturday":
			case "sunday":
			case "sunday,":
			case "utc": tokenCount += 1; break;
			case "on": if(tokenCount > 0) {
				tokenCount += 1;
			}
			break;
			case "am.":
			case "pm.":
			case "am":
			case "pm": tokenCount += 1; containTime = true; break;
			case "bc.":
			case "bc": tokenCount +=1; isBC = true; containDate = true; break;
			case "ad.":
			case "ad": tokenCount +=1; isAD = true; break;
			default:
				if (patternTime.matcher(token).matches()) {
//				if (token.matches("\\d{1,2}(:\\d{1,2}){1,2}\\s*[aApPmM\\.,]*")) {
					if (containTime) {
						this.processTemporal(stream);
						break;
					}
					containTime = true;
					tokenCount += 1;
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
				} else if (patternNumber.matcher(token).matches()) {
//				} else if (token.matches("\\d{1,4}[,\\.]*")) {
					containDate = true;
					tokenCount += 1;
					token = token.replaceAll("[,\\.]", "");
					int length = token.length();
					if (length == 4) {
						if (!this.setYear(token)) {
							tokenCount -= 1;
							this.processTemporal(stream);
							break;
						}
					} else if (length == 3) {
						if (!this.setYear("0" + token)) {
							tokenCount -= 1;
							this.processTemporal(stream);
							break;
						}
					} else if (length == 2) {
						if ((Integer.valueOf(token) > 31)) {
							if (!this.setYear("00" + token)) {
								tokenCount -= 1;
								this.processTemporal(stream);
								break;
							}
						} else {
							if (!this.setDay(token)) {
								tokenCount -= 1;
								this.processTemporal(stream);
								break;
							}
						}
					} else {
						if (!this.setDay("0" + token)) {
							tokenCount -= 1;
							this.processTemporal(stream);
							break;
						}
					}
				} else if (patternTwoYearPattern.matcher(token).matches()) {
					containDate = true;
					tokenCount += 1;
					char[] charsOfToken = token.toCharArray();
					StringBuilder sb1 = new StringBuilder();
					int i = 0;
					int markStart = 0;
					int markEnd = 0;
					int indexOfLastNumber = 0;
					for (; i < charsOfToken.length; i++) {
						char c = charsOfToken[i];
						if (c >= '0' && c <= '9') {
							sb1.append(c);
						} else {
							markStart = i;
							break;
						}
					}
					String year1 = sb1.toString();
					StringBuilder sb2 = new StringBuilder();
					if (!this.setYear(year1)) {
						tokenCount -= 1;
						this.processTemporal(stream);
						break;
					} else {
						for (; i < charsOfToken.length; i++) {
							char c = charsOfToken[i];
							if (c >= '0' && c <= '9') {
								if (markEnd == 0) {
									markEnd = i;
								}
								sb2.append(c);
								indexOfLastNumber = i;
							}
						}
					}
					String year2 = sb2.toString();
					if (year2.length() < 4) {
						year2 = year1.substring(0, 4 - year2.length()) + year2;
					}
					String temporal = "";
					if (month == null) {
						month = "01";
					}
					if (day == null) {
						day = "01";
					}
					if (isBC) {
						temporal += "-";
					}
					temporal += year1 + month + day + token.substring(markStart, markEnd) + year2 + month + day + token.substring(indexOfLastNumber + 1);
					if (stream.hasNext()) {
						this.processTemporal(stream, false, temporal);
					} else {
						this.processTemporal(stream, true, temporal);
					}
					break;
				} else {
					this.processTemporal(stream);
				}
			}
		}
		this.processTemporal(stream, true);
		stream.reset();
	}
	
	private boolean setMonth(String s) {
		if (this.month == null) {
			this.month = s;
			this.containDate = true;
			return true;
		} else {
			return false;
		}
	}
	
	private boolean setYear(String s) {
		if (this.year == null) {
			this.year = s;
			this.containDate = true;
			return true;
		} else {
			return false;
		}
	}
	
	private boolean setDay(String s) {
		if (this.day == null) {
			this.day = s;
			this.containDate = true;
			return true;
		} else {
			return false;
		}
	}
	
	private void processTemporal(TokenStream stream) {
			this.processTemporal(stream, false, null);
	}
	
	private void processTemporal(TokenStream stream, boolean lastToken) {
		this.processTemporal(stream, lastToken, null);
	}
	
	private void processTemporal(TokenStream stream, boolean lastToken, String dualYear) {
		if (tokenCount > 0) {
			if (!lastToken) {
				stream.previous();
			}
			if (!containTime && year == null && month == null) {
				this.reset();
				return;
			}
			String temporal = "";
			String retainString = stream.previous();
			stream.next();
			retainString = retainString.replaceAll("[0-9A-z:]*", "");
			while (tokenCount > 0) {
				tokenCount -= 1;
				stream.previous();
				stream.remove();
			}
			if (dualYear != null) {
				temporal = dualYear;
				retainString = "";
			} else if (containDate) {
				year = year == null ? "1900" : year;
				if (month == null) {
					month = "01";
				}
				if (day == null) {
					day = "01";
				}
				if (isBC) {
					temporal += "-";
				}
				temporal += year + month + day;
			}
			if (containTime) {
				hour = hour == null ? "00" : hour;
				minute = minute == null ? "00" : minute;
				second = second == null ? "00" : second;
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
			if (retainString != "" || !retainString.isEmpty()) {
				temporal += retainString;
			}
			stream.add(temporal);
//			stream.previous();
			this.reset();
		}
	}
	private void reset() {
		this.isBC = false;
		this.isAD = false;
		this.containDate = false;
		this.containTime = false;
		this.containPM = false;
		this.year = null;
		this.month = null;
		this.day = null;
		this.hour = null;
		this.minute = null;
		this.second = null;
		this.tokenCount = 0;
	}

}

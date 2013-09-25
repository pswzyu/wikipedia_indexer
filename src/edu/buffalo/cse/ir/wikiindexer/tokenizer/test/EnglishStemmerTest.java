package edu.buffalo.cse.ir.wikiindexer.tokenizer.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

import java.util.Properties;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import edu.buffalo.cse.ir.wikiindexer.IndexerConstants;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;


@RunWith(Parameterized.class)
public class EnglishStemmerTest extends TokenizerRuleTest {
	public EnglishStemmerTest(Properties props) {
		super(props, IndexerConstants.STEMMERRULE);
	}
	
	@Test
	public void testRule() {
		if (rule == null) {
			fail("Rule not implemented");
		} else {
			try {
//				assertArrayEquals(new Object[] { "aa" },runtest("aae"));
//				assertArrayEquals(new Object[] { "ea" },runtest("eae"));
//				assertArrayEquals(new Object[] { "ia" },runtest("iae"));
//				assertArrayEquals(new Object[] { "oa" },runtest("oae"));
//				assertArrayEquals(new Object[] { "ua" },runtest("uae"));
				assertArrayEquals(new Object[] { "ab" },runtest("abe"));
				assertArrayEquals(new Object[] { "eb" },runtest("ebe"));
				assertArrayEquals(new Object[] { "ib" },runtest("ibe"));
				assertArrayEquals(new Object[] { "ob" },runtest("obe"));
				assertArrayEquals(new Object[] { "ub" },runtest("ube"));
				assertArrayEquals(new Object[] { "ac" },runtest("ace"));
				assertArrayEquals(new Object[] { "ec" },runtest("ece"));
				assertArrayEquals(new Object[] { "ic" },runtest("ice"));
				assertArrayEquals(new Object[] { "oc" },runtest("oce"));
				assertArrayEquals(new Object[] { "uc" },runtest("uce"));
				assertArrayEquals(new Object[] { "ad" },runtest("ade"));
				assertArrayEquals(new Object[] { "ed" },runtest("ede"));
				assertArrayEquals(new Object[] { "id" },runtest("ide"));
				assertArrayEquals(new Object[] { "od" },runtest("ode"));
				assertArrayEquals(new Object[] { "ud" },runtest("ude"));
//				assertArrayEquals(new Object[] { "ae" },runtest("aee"));
//				assertArrayEquals(new Object[] { "ee" },runtest("eee"));
//				assertArrayEquals(new Object[] { "ie" },runtest("iee"));
//				assertArrayEquals(new Object[] { "oe" },runtest("oee"));
//				assertArrayEquals(new Object[] { "ue" },runtest("uee"));
				assertArrayEquals(new Object[] { "af" },runtest("afe"));
				assertArrayEquals(new Object[] { "ef" },runtest("efe"));
				assertArrayEquals(new Object[] { "if" },runtest("ife"));
				assertArrayEquals(new Object[] { "of" },runtest("ofe"));
				assertArrayEquals(new Object[] { "uf" },runtest("ufe"));
			} catch (TokenizerException e) {
			}
		}
	}
}

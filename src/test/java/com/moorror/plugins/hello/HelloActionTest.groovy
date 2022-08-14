package com.moorror.plugins.hello

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import com.intellij.usageView.UsageInfo
import org.junit.Test

class HelloActionTest extends LightCodeInsightFixtureTestCase {
    void testActionPerformed() {

    }

    @Test
    public void testCompletion() {

        System.out.println("---test---");
        System.out.println(myFixture);
//        myFixture.configureByFiles("CompleteTestData.java", "DefaultTestData.simple");
//        myFixture.complete(CompletionType.BASIC, 1);
//        List<String> strings = myFixture.getLookupElementStrings();
//        assertTrue(strings.containsAll(Arrays.asList("key with spaces", "language", "message", "tab", "website")));
//        assertEquals(5, strings.size());
    }
    public void testRename() {
        myFixture.configureByFiles("RenameTestData.java", "RenameTestData.simple");
        myFixture.renameElementAtCaret("websiteUrl");
        myFixture.checkResultByFile("RenameTestData.simple", "RenameTestDataAfter.simple", false);
    }

    @Test
    public void testFindUsages() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("FindUsagesTestData.simple", "FindUsagesTestData.java");
        assertEquals(1, usageInfos.size());
    }
}

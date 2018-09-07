import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.checkpoint.CheckpointFactory as CheckpointFactory
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as MobileBuiltInKeywords
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testcase.TestCaseFactory as TestCaseFactory
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testdata.TestDataFactory as TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository as ObjectRepository
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WSBuiltInKeywords
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUiBuiltInKeywords
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

def config = TestDataFactory.findTestData('Configuration')

WebUI.openBrowser(config.getValue('url', 1))

//********************************************************Login as an admin********************************************************//

WebUI.setText(findTestObject('Monitoring/Downtimes/input_useralias'), config.getValue('login', 1))

WebUI.setText(findTestObject('Monitoring/Downtimes/input_password'), config.getValue('password', 1))

WebUI.click(findTestObject('General/Login/input_submitLogin'))

//*********************************************************Go to Downtimes*********************************************************//

CustomKeywords.'custom.NavigationMonitoring.accessDowntimes'()

WebUI.waitForPageLoad(3)

//*****************************************************Delete all the downtimes****************************************************//

if(WebUI.verifyElementPresent(findTestObject('Monitoring/Downtimes/input_Cancel button'),
	1, FailureHandling.OPTIONAL)){
	WebUI.click(findTestObject('General/input_Checkall'))
	
	WebUI.click(findTestObject('Monitoring/Downtimes/input_Cancel button'), FailureHandling.OPTIONAL)
	
	WebUI.acceptAlert()
	
	WebUI.delay(1)
}

//********************************************************Create a downtime********************************************************//

WebUI.click(findTestObject('Monitoring/Downtimes/a_Add a downtime'))

//This is to avoid Chrome's failure
WebUI.delay(1)

//This design the button 'servicegroup' of the downtime type
def element = WebUI.modifyObjectProperty(findTestObject('Monitoring/Downtimes/input_downtimeType'),
	'id', 'equals', 'hostgroup', true)

WebUI.click(element)

WebUI.waitForElementVisible(findTestObject('Monitoring/Downtimes/input_Services'), 3)

element = WebUI.modifyObjectProperty(findTestObject('Monitoring/Downtimes/input_Services'),
	'placeholder', 'equals', 'Hostgroups', true)

WebUI.click(element)

def hgfile = TestDataFactory.findTestData('Host group')

element = WebUI.modifyObjectProperty(findTestObject('Monitoring/Downtimes/li_pouic - Ping'),
	'text', 'equals', config.getValue('TimeIndicator', 1) + hgfile.getValue('hostGroupName', 1), true)

WebUI.click(element)

WebUI.setText(findTestObject('Monitoring/Downtimes/textarea_comment'), 'Katalon comment')

WebUI.click(findTestObject('General/input_submitA'))

//Wait to be sure Edge is not too fast
WebUI.waitForPageLoad(3)

//Let one second to be sure the downtime is correctly configured and will be displayed
WebUI.delay(1)

//************************************************Go to Status Details services page***********************************************//

CustomKeywords.'custom.NavigationMonitoring.accessStatusDetailsServices'()

//*******************************************************Verify the downtime*******************************************************//

//This put the service status to 'All'
WebUI.selectOptionByValue(findTestObject('Monitoring/Status details/Services/select_Unhandled Problems'), 'svc', true)

def hostFile = TestDataFactory.findTestData('Host data')

//These lines search for the service affected by the downtime
def search = WebUI.modifyObjectProperty(findTestObject("General/input_Search"), 'name', 'equals', 'host_search', true)

WebUI.setText(search, config.getValue('TimeIndicator', 1) + hostFile.getValue('hostName', 1) + '1_1')

def servicesFile = TestDataFactory.findTestData('Services')

search = WebUI.modifyObjectProperty(search, 'name', 'equals', 'search', true)

for(def index : (1..servicesFile.getRowNumbers())){
	WebUI.setText(search, servicesFile.getValue(1, index))

	WebUI.delay(1)

	WebUI.verifyElementPresent(findTestObject('Monitoring/Downtimes/img_Service downtime icon'), 3)
}

search = WebUI.modifyObjectProperty(findTestObject("General/input_Search"), 'name', 'equals', 'host_search', true)

WebUI.setText(search, config.getValue('TimeIndicator', 1) + hostFile.getValue('hostName', 1) + '1_2')

search = WebUI.modifyObjectProperty(search, 'name', 'equals', 'search', true)

for(def index : (1..servicesFile.getRowNumbers())){
	WebUI.setText(search, servicesFile.getValue(1, index))
	
	WebUI.delay(1)

	WebUI.verifyElementPresent(findTestObject('Monitoring/Downtimes/img_Service downtime icon'), 3)
}

WebUI.click(findTestObject('Old menu/a_Logout'))

WebUI.closeBrowser()
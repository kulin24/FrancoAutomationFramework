Feature: This is a World Bank Data feature
  This feature is used to process world bank data of pPpulation, GDP & GDP growth for all countries.

  @Testid_101
  Scenario: Process the world bank data for all the countries
		Given open world bank web site
		And Home page of world bank web site is displayed
		When click on Countries tab
		Then Countries page of world bank web site is displayed 
		When read and note data for all countries on countries tab of world bank web site
		And click on Home tab
		Then Home page of world bank web site is displayed
	    
		Then log top 3 countries as per population
		And log top 3 countries as per GDP
	    And log top 3 countries as per GDP Growth
	    And export all the country data


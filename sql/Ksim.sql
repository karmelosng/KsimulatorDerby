DELETE TABLE AVAILABLE_COMPONENTS FROM KSIMULATOR;
CREATE TABLE AVAILABLE_COMPONENTS(SIMSTATE_ID INT PRIMARY KEY, AVAILABLECOMPONENTS_ID INT NOT NULL);
INSERT INTO available_components (SimState_id, availableComponents_id) VALUES
(244, 2),
(244, 3);

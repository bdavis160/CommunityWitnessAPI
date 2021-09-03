CREATE TABLE IF NOT EXISTS Witness (
        ID serial PRIMARY KEY,
        Name text,
        Rating double precision,
        Location text
);

CREATE TABLE IF NOT EXISTS Investigator (
        ID serial PRIMARY KEY,
        Name text,
		Organization text,
        OrganizationType text,
        Rating double precision
);

CREATE TABLE IF NOT EXISTS Report (
        ID serial PRIMARY KEY,
        Resolved boolean,
        Description text,
        Time timestamp with time zone,
        Location text,
        WitnessID integer,
        FOREIGN KEY (WitnessID) REFERENCES Witness(ID)
);

CREATE TABLE IF NOT EXISTS ReportComments (
	   ID serial PRIMARY KEY,
	   ReportID integer,
	   InvestigatorID integer,
	   Contents text,
	   FOREIGN KEY (ReportID) REFERENCES Report(ID),
	   FOREIGN KEY (InvestigatorID) REFERENCES Investigator(ID)
);

CREATE TABLE IF NOT EXISTS ReportInvestigations (
	   ReportID integer,
	   InvestigatorID integer,
	   PRIMARY KEY (ReportID, InvestigatorID),
	   FOREIGN KEY (ReportID) REFERENCES Report(ID),
	   FOREIGN KEY (InvestigatorID) REFERENCES Investigator(ID)
);

CREATE TABLE IF NOT EXISTS Evidence (
        ID serial PRIMARY KEY,
        Title text,
        Type text,
        Timestamp timestamp with time zone,
        ReportID integer,
        data bytea,
        FOREIGN KEY (ReportID) REFERENCES Report(ID)
);

CREATE TABLE IF NOT EXISTS Chat (
	   ID serial PRIMARY KEY,
	   ReportID integer,
	   InvestigatorID integer,
	   Message text,
	   time timestamp with time zone,
	   FOREIGN KEY (ReportID) REFERENCES Report(ID),
	   FOREIGN KEY (InvestigatorID) REFERENCES Investigator(ID)
);

CREATE TABLE IF NOT EXISTS Accounts (
	   Username text PRIMARY KEY,
	   PasswordHash text NOT NULL,
	   InvestigatorId integer NOT NULL,
	   FOREIGN KEY (InvestigatorID) REFERENCES Investigator(ID)
);

CREATE TABLE IF NOT EXISTS ApiKeys (
	   ApiKey text PRIMARY KEY,
	   WitnessId integer,
	   InvestigatorID integer,
	   FOREIGN KEY (WitnessID) REFERENCES Witness(ID),
	   FOREIGN KEY (InvestigatorID) REFERENCES Investigator(ID)
);

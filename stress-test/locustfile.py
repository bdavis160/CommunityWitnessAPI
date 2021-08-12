import time
from locust import HttpUser, task, between
from random import choice
from requests import Response, exceptions


# This wait time is a really rough estimate of how long users will wait between pages.
ESTIMATED_WAIT_TIME = between(5, 30)


class CommunityWitnessUser(HttpUser):
    """A generic class with functionality common to all Community Witness Users."""
    # This tells locust not to directly use this class for stress testing
    abstract = True
    wait_time = ESTIMATED_WAIT_TIME

    def __init__(self, *args, **kwargs):
        """Sets up the user with an empty reports list."""
        self.reports = []
        super().__init__(*args, **kwargs)

    def grab_evidence(self, evidence_id):
        """GETs the evidence with the given id."""
        self.client.get(f"/evidence/{evidence_id}")

    def grab_report_comment(self, comment_id):
        """GETs the report comment with the given id."""
        self.client.get(f"/reportComments/{comment_id}")

    def extract_reports(self, user_object):
        """Extracts the ids of this users reports from its object."""
        try:
            self.reports = user_object.json()["reports"]
        except JSONDecodeError:
            # Since locust logs failures already, just ignore bad responses
            return

    @task
    def grab_report(self):
        """Randomly GET one of the reports this user filed (for witnesses) or is investigating (for investigators),
        along with the evidence and comments on it like an API client would.
        """
        if not self.reports:
            return
        response = self.client.get(f"/reports/{choice(self.reports)}")
        try:
            report_json = response.json()
            evidence_ids = report_json["evidence"]
            comment_ids = report_json["comments"]

            for evidence_id in evidence_ids:
                self.grab_evidence(evidence_id)

            for comment_id in comment_ids:
                self.grab_report_comment(comment_id)
        except JSONDecodeError:
            # Again locust logs failures
            return


class WitnessUser(CommunityWitnessUser):
    """A class for stress testing the parts of the API that witnesses use."""
    weight = 10 # witness users are much more common than investigators

    def __init__(self, *args, **kwargs):
        """Setup class variables, by running supers init."""
        self.id = 1
        super().__init__(*args, **kwargs)

    def on_start(self):
        """Logs in the user and grabs their user data."""
        # TODO: send login request here
        self.grab_witness()

    @task
    def grab_witness(self):
        """GETs the data about this witness from the API, and saves it if needed."""
        response = self.client.get(f"/witnesses/{self.id}")
        if not self.reports:
            self.extract_reports(response)


class InvestigatorUser(CommunityWitnessUser):
    """A class for stress testing the parts of the API that investigators use."""
    weight = 1

    def __init__(self, *args, **kwargs):
        """Setup class variables, by running supers init."""
        self.id = 1
        super().__init__(*args, **kwargs)
    
    def on_start(self):
        """Logs in the user and grabs their user data."""
        # TODO: send login request here
        self.grab_investigator()

    @task
    def grab_investigator(self):
        """GETs the data about this investigator from the API, and saves it as needed."""
        response = self.client.get(f"/investigators/{self.id}")
        if not self.reports:
            self.extract_reports(response)

    

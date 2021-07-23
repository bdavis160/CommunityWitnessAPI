import time
from locust import HttpUser, task, between

class CommunityWitnessUser(HttpUser):
    wait_time = between(1, 2.5)

    @task
    def grab_report(self):
        self.client.get("/reports/2")


    @task
    def grab_witnesses(self):
        self.client.get("/witnesses/1")


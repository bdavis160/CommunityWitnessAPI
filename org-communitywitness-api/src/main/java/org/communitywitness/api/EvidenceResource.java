package org.communitywitness.api;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.Base64;

@Path("/evidence")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EvidenceResource {
	/**
	 * Creates new evidence with the data contained in the sent object.
	 * @param newEvidenceRequestData - an object containing the information about the evidence
	 * @return the actual id of the newly created evidence
	 * @throws WebApplicationException if the report the evidence is associated with doesn't exist
	 */
	@RolesAllowed({UserRoles.WITNESS})
	@POST
	public int createEvidence(NewEvidenceRequest newEvidenceRequestData, @Context AuthenticatedUser user) throws WebApplicationException, SQLException, IOException {
		String dirname;
		String filename;
		File directory;
		FileOutputStream out = null;

		// sets write directory to windows project dir if api is running locally
		boolean localAPI = System.getProperty("os.name").equals("Windows 10");

		// check if the report exists and if it belongs to this user
		try {
			Report relevantReport = new Report(newEvidenceRequestData.getReportId());
			
			if (user.getId() != relevantReport.getWitnessId())
				throw new WebApplicationException(AuthenticationFilter.unauthorizedAccessResponse("You can only post evidence for your reports."));
		} catch (SQLException exception) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		if (localAPI)
		{
			dirname = System.getProperty("user.dir") + "\\"
					+ newEvidenceRequestData.getReportId() + "\\";
		} else {
			dirname = "/home/kaz4_pdx_edu/communityWitnessImages/"
					+ newEvidenceRequestData.getReportId() + "/";
		}

		directory = new File (dirname);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		try {
			byte[] byteArray = Base64.getMimeDecoder().decode(newEvidenceRequestData.getData());
			filename = dirname
					+ newEvidenceRequestData.getTimestamp().toString().replaceAll("[^a-zA-Z0-9]", "_")
					+ URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(byteArray)).replaceAll("image/", ".");			out = new FileOutputStream(filename);
			out.write(byteArray);

		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}

		newEvidenceRequestData.setLink(dirname);
		Evidence newEvidence = new Evidence(newEvidenceRequestData);
		return newEvidence.writeToDb();
	}
	
	/**
	 * Returns the evidence with the given id.
	 * @param evidenceId - the id of the evidence
	 * @return the evidence data
	 * @throws WebApplicationException if no evidence with the given id is found
	 */
	@RolesAllowed({UserRoles.INVESTIGATOR, UserRoles.WITNESS})
	@GET
	@Path("/{evidenceId}")
	public Evidence getEvidence(@PathParam("evidenceId") int evidenceId, @Context AuthenticatedUser user) throws WebApplicationException {
		Evidence requestedEvidence;
		
		try {
			requestedEvidence = new Evidence(evidenceId);
			
			if (user.isUserInRole(UserRoles.WITNESS)) {
				Report relevantReport = new Report(requestedEvidence.getReportId());
				
				if (user.getId() != relevantReport.getWitnessId())
					throw new WebApplicationException(AuthenticationFilter.unauthorizedAccessResponse("You can only view evidence for your reports."));
			}
		} catch (SQLException exception) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		return requestedEvidence;
	}
}

package org.communitywitness.api;

import jakarta.ws.rs.*;
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
	 * TODO: figure out how to consume files associated with the evidence
	 * @param newEvidenceRequestData - an object containing the information about the evidence
	 * @return the actual id of the newly created evidence
	 * @throws WebApplicationException if the report the evidence is associated with doesn't exist
	 */
	@POST
	public int createEvidence(NewEvidenceRequest newEvidenceRequestData) throws WebApplicationException, SQLException, IOException {
		String dirname;
		String filename;
		File directory;
		FileOutputStream out = null;

		// sets write directory to windows project dir if api is running locally
		boolean localAPI = System.getProperty("os.name").equals("Windows 10");

		// check if the report exists
		try {
			new Report(newEvidenceRequestData.getReportId());
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
	@GET
	@Path("/{evidenceId}")
	public Evidence getEvidence(@PathParam("evidenceId") int evidenceId) throws WebApplicationException {
		Evidence requestedEvidence;
		
		try {
			requestedEvidence = new Evidence(evidenceId);
		} catch (SQLException exception) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		return requestedEvidence;
	}

	// TODO: Do we still need this?
	/*
	/**
	 * A resource that doesn't rely on the database for testing if it's running properly on local machines.
	 * @return an evidence object with simple contents
	 */
		/*
	@GET
	@Path("/test")
	public Evidence testEvidence() {
		Evidence dummyData = new Evidence();
		dummyData.setId(0);
		dummyData.setLink("link");
		dummyData.setReportId(0);
		dummyData.setTimestamp(new Date());
		dummyData.setTitle("title");
		dummyData.setType("type");
		
		return dummyData;
	}
	*/
}

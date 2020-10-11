package com.myorg;

import software.amazon.awscdk.core.App;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;

import java.io.IOException;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class InfrastructureTest {

	private final static ObjectMapper JSON = new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true);

	@Test
	public void testStack() throws IOException {
		App app = new App();
		InfrastructureStack stack = new InfrastructureStack(app, "test");

		JsonNode actual = JSON.valueToTree(app.synth().getStackArtifact(stack.getArtifactId()).getTemplate());
		assertThat(actual.get("Resources"))
				.anyMatch(jsonNode -> "AWS::Route53::HostedZone".equals(jsonNode.get("Type").asText()));
	}

}

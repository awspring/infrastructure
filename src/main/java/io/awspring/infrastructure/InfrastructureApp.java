package io.awspring.infrastructure;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Environment;
import software.amazon.awscdk.core.StackProps;

public class InfrastructureApp {

	public static void main(final String[] args) {
		App app = new App();

		// certificates used by CloudFront must be created in us-east-1 region
		new InfrastructureStack(app, "InfrastructureStack",
				StackProps.builder().env(Environment.builder().region("us-east-1").build()).build());

		app.synth();
	}

}

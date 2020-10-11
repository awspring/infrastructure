package com.myorg;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.core.Tags;
import software.amazon.awscdk.services.route53.HostedZone;
import software.amazon.awscdk.services.route53.HostedZoneProps;

public class InfrastructureStack extends Stack {

	public InfrastructureStack(final Construct scope, final String id) {
		this(scope, id, null);
	}

	public InfrastructureStack(final Construct scope, final String id, final StackProps props) {
		super(scope, id, props);

		// Create Hosted Zone for awspring.io domain
		HostedZone hostedZone = new HostedZone(this, "HostedZone",
				HostedZoneProps.builder().zoneName("awspring.io").build());
		Tags.of(hostedZone).add("component", "website");
	}

}

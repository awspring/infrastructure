package io.awspring.infrastructure;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.core.Tags;
import software.amazon.awscdk.services.certificatemanager.Certificate;
import software.amazon.awscdk.services.certificatemanager.CertificateProps;
import software.amazon.awscdk.services.certificatemanager.CertificateValidation;
import software.amazon.awscdk.services.cloudfront.Behavior;
import software.amazon.awscdk.services.cloudfront.CloudFrontWebDistribution;
import software.amazon.awscdk.services.cloudfront.CloudFrontWebDistributionProps;
import software.amazon.awscdk.services.cloudfront.S3OriginConfig;
import software.amazon.awscdk.services.cloudfront.SourceConfiguration;
import software.amazon.awscdk.services.cloudfront.ViewerCertificate;
import software.amazon.awscdk.services.cloudfront.ViewerCertificateOptions;
import software.amazon.awscdk.services.route53.ARecord;
import software.amazon.awscdk.services.route53.ARecordProps;
import software.amazon.awscdk.services.route53.CnameRecord;
import software.amazon.awscdk.services.route53.CnameRecordProps;
import software.amazon.awscdk.services.route53.HostedZone;
import software.amazon.awscdk.services.route53.HostedZoneProps;
import software.amazon.awscdk.services.route53.RecordTarget;
import software.amazon.awscdk.services.route53.targets.CloudFrontTarget;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.BucketProps;

import java.util.Arrays;
import java.util.Collections;

public class InfrastructureStack extends Stack {

	public InfrastructureStack(final Construct scope, final String id) {
		this(scope, id, null);
	}

	public InfrastructureStack(final Construct scope, final String id, final StackProps props) {
		super(scope, id, props);

		final String domain = "awspring.io";

		// Create Hosted Zone for awspring.io domain
		HostedZone hostedZone = new HostedZone(this, "HostedZone", HostedZoneProps.builder().zoneName(domain).build());
		Tags.of(hostedZone).add("component", "website");

		// Create a certificate for Cloudfront
		Certificate certificate = new Certificate(this, "awspring.io-certificate",
				CertificateProps.builder().domainName(hostedZone.getZoneName())
						.subjectAlternativeNames(Collections.singletonList("*." + hostedZone.getZoneName()))
						.validation(CertificateValidation.fromDns(hostedZone)).build());
		Tags.of(certificate).add("component", "website");

		// ---------------------- https://awspring.io ----------------------

		// Create a S3 bucket where static site is hosted
		Bucket bucket = new Bucket(this, "website-bucket", BucketProps.builder().bucketName("awspring-website")
				.publicReadAccess(true).websiteIndexDocument("index.html").build());
		Tags.of(bucket).add("component", "website");

		// Create Cloudfront (CDN) distribution that links to S3
		CloudFrontWebDistribution cloudfront = new CloudFrontWebDistribution(this, "website-distribution",
				CloudFrontWebDistributionProps.builder()
						.viewerCertificate(ViewerCertificate.fromAcmCertificate(certificate,
								ViewerCertificateOptions.builder().aliases(Collections.singletonList(domain)).build()))
						.originConfigs(Collections.singletonList(SourceConfiguration.builder()
								.s3OriginSource(S3OriginConfig.builder().s3BucketSource(bucket).build())
								.behaviors(Arrays.asList(Behavior.builder().isDefaultBehavior(true).build())).build()))
						.build());
		Tags.of(cloudfront).add("component", "website");

		// Create a Route 53 record that links to Cloudfront
		ARecord aRecord = new ARecord(this, "awspring-arecord", ARecordProps.builder().zone(hostedZone)
				.target(RecordTarget.fromAlias(new CloudFrontTarget(cloudfront))).build());
		Tags.of(aRecord).add("component", "website");

		// ---------------------- https://docs.awspring.io ----------------------

		// Create a S3 bucket where docs are hosted
		Bucket docsBucket = new Bucket(this, "docs-bucket", BucketProps.builder().bucketName("awspring-docs")
				.publicReadAccess(true).websiteIndexDocument("index.html").build());
		Tags.of(bucket).add("component", "website");

		// Create Cloudfront (CDN) distribution that links to S3
		new CloudFrontWebDistribution(this, "docs-distribution",
				CloudFrontWebDistributionProps.builder()
						.viewerCertificate(ViewerCertificate.fromAcmCertificate(certificate,
								ViewerCertificateOptions.builder().aliases(Collections.singletonList("docs." + domain))
										.build()))
						.originConfigs(Collections.singletonList(SourceConfiguration.builder()
								.s3OriginSource(S3OriginConfig.builder().s3BucketSource(docsBucket).build())
								.behaviors(Arrays.asList(Behavior.builder().isDefaultBehavior(true).build())).build()))
						.build());
		Tags.of(cloudfront).add("component", "website");

		CnameRecord cnameRecord = new CnameRecord(this, "awspring-docs-cname",
				CnameRecordProps.builder().zone(hostedZone).domainName(cloudfront.getDistributionDomainName())
						.recordName("docs.awspring.io").build());
		Tags.of(cnameRecord).add("component", "website");
	}

}

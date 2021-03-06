/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/
package org.hyperledger.fabric.shim.ext.sbe.impl;

import org.hyperledger.fabric.protos.common.MspPrincipal.MSPPrincipal;
import org.hyperledger.fabric.protos.common.MspPrincipal.MSPPrincipal.Classification;
import org.hyperledger.fabric.protos.common.MspPrincipal.MSPRole;
import org.hyperledger.fabric.protos.common.MspPrincipal.MSPRole.MSPRoleType;
import org.hyperledger.fabric.protos.common.Policies.SignaturePolicy;
import org.hyperledger.fabric.protos.common.Policies.SignaturePolicy.NOutOf;
import org.hyperledger.fabric.protos.common.Policies.SignaturePolicyEnvelope;

import java.util.Arrays;
import java.util.List;

/**
 * Utility to create {@link SignaturePolicy} and {@link SignaturePolicyEnvelope}
 */
public class StateBasedEndorsementUtils {
    /**
     * Creates a SignaturePolicy requiring a given signer's signature
     *
     * @param index
     * @return
     */
    static SignaturePolicy signedBy(int index){
        return SignaturePolicy.newBuilder()
                .setSignedBy(index).build();
    }

    /**
     * Creates a policy which requires N out of the slice of policies to evaluate to true
     *
     * @param n
     * @param policies
     * @return
     */
    static SignaturePolicy nOutOf(int n, List<SignaturePolicy> policies) {
        return SignaturePolicy
                .newBuilder()
                .setNOutOf(NOutOf
                        .newBuilder()
                        .setN(n)
                        .addAllRules(policies)
                        .build())
                .build();
    }

    /**
     * Creates a {@link SignaturePolicyEnvelope}
     * requiring 1 signature from any fabric entity, having the passed role, of the specified MSP
     *
     * @param mspId
     * @param role
     * @return
     */
    static SignaturePolicyEnvelope signedByFabricEntity(String mspId, MSPRoleType role) {
        // specify the principal: it's a member of the msp we just found
        MSPPrincipal principal = MSPPrincipal
                .newBuilder()
                .setPrincipalClassification(Classification.ROLE)
                .setPrincipal(MSPRole
                        .newBuilder()
                        .setMspIdentifier(mspId)
                        .setRole(role)
                        .build().toByteString())
                .build();

        // create the policy: it requires exactly 1 signature from the first (and only) principal
        return SignaturePolicyEnvelope
                .newBuilder()
                .setVersion(0)
                .setRule(nOutOf(1, Arrays.asList(signedBy(0))))
                .addIdentities(principal)
                .build();

    }


}

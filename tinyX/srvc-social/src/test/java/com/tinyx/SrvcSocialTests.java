package com.tinyx;

import static org.junit.jupiter.api.Assertions.*;

import com.tinyx.repository.LookupRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;
import javax.inject.Inject;

public class SrvcSocialTests {

  @Inject LookupRepository srvcSocialRepository;

  @ApplicationScoped SrvcSocialRepositoryTests srvcSocialRepositoryTest;

  public void testGetTargetLikersEndpoint() {
    String input = "550e8400-e29b-41d4-a716-446655440001";
    UUID postId = UUID.nameUUIDFromBytes(input.getBytes());
    UUID userId = UUID.randomUUID();

    // Par sécurité, commenter tous les codes delete Neo4j quand on merge develop
    srvcSocialRepositoryTest.deleteUserAndPost();
    srvcSocialRepositoryTest.createUserAndPost(userId, postId);
    var userIdResponse = srvcSocialRepository.getUserIdFromPost(postId);
    // srvcSocialRepositoryTest.deleteUserAndPost();

    System.out.println(userIdResponse);
    assertEquals(userIdResponse, userId);
  }
}

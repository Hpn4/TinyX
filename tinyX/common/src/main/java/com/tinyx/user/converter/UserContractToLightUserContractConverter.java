package com.tinyx.user.converter;

import com.tinyx.user.contracts.LightUserContract;
import com.tinyx.user.contracts.UserContract;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserContractToLightUserContractConverter {
  public LightUserContract convert(UserContract contract) {
    return new LightUserContract(contract.id, contract.userName, contract.creationDate);
  }

  public List<LightUserContract> convert(List<UserContract> contracts) {
    return contracts.stream().map(this::convert).collect(Collectors.toList());
  }
}

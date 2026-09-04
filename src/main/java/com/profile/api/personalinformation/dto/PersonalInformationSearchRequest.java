package com.profile.api.personalinformation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonalInformationSearchRequest {

    private int page = 0;
    private int size = 10;
    private String sortBy = "createdAt";
    private String sortDirection = "desc";
    private String search;
    private String firstName;
    private String lastName;
    private String location;
}

package com.miaxis.btlockcommanderdemo.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class PersonDto implements Parcelable {
	private Long id;
	private String personId;
	private String name;
	private String alias;

    public PersonDto(Long id, String personId, String name, String alias) {
        this.id = id;
        this.personId = personId;
        this.name = name;
        this.alias = alias;
    }

    public Long getId() {
		return id;
	}
	public String getPersonId() {
		return personId;
	}
	public String getName() {
		return name;
	}
	public String getAlias() {
		return alias;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.personId);
        dest.writeString(this.name);
        dest.writeString(this.alias);
    }

    public PersonDto() {
    }

    protected PersonDto(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.personId = in.readString();
        this.name = in.readString();
        this.alias = in.readString();
    }

    public static final Parcelable.Creator<PersonDto> CREATOR = new Parcelable.Creator<PersonDto>() {
        @Override
        public PersonDto createFromParcel(Parcel source) {
            return new PersonDto(source);
        }

        @Override
        public PersonDto[] newArray(int size) {
            return new PersonDto[size];
        }
    };
}
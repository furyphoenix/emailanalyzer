package neos.tool.googlemap.geocode;

public enum AddressType {
	/**
	 * indicates a precise street address.
	 */
	STREET_ADDRESS,
	
	/**
	 * indicates a named route (such as "US 101").
	 */
	ROUTE,
	
	/**
	 * indicates a major intersection, usually of two major roads.
	 */
	INTERSECTION,
	
	/**
	 * indicates a political entity. Usually, this type indicates a polygon of some civil administration.
	 */
	POLITICAL,
	
	/**
	 * indicates the national political entity, and is typically the highest order type returned by the Geocoder.
	 */
	COUNTRY,
	
	/**
	 * indicates a first-order civil entity below the country level. Within the United States, these administrative levels are states. Not all nations exhibit these administrative levels.
	 */
	ADMINISTRATIVE_AREA_LEVEL_1,
	//ADMINISTRATIVE_LEVEL_1,
	
	
	/**
	 * indicates a second-order civil entity below the country level. Within the United States, these administrative levels are counties. Not all nations exhibit these administrative levels.
	 */
	ADMINISTRATIVE_AREA_LEVEL_2,
	
	/**
	 * indicates a third-order civil entity below the country level. This type indicates a minor civil division. Not all nations exhibit these administrative levels.
	 */
	ADMINISTRATIVE_AREA_LEVELL_3,
	
	/**
	 * indicates a commonly-used alternative name for the entity.
	 */
	COLLOQUIAL_AREA,
	
	/**
	 * indicates an incorporated city or town political entity.
	 */
	LOCALITY,
	
	/**
	 * indicates an first-order civil entity below a locality
	 */
	SUBLOCALITY,
	
	/**
	 * indicates a named neighborhood
	 */
	NEIGHBOURHOOD,
	
	/**
	 * indicates a named location, usually a building or collection of buildings with a common name
	 */
	PREMISE,
	
	/**
	 * indicates a first-order entity below a named location, usually a singular building within a collection of buildings with a common name
	 */
	SUBPREMISE,
	
	/**
	 * indicates a postal code as used to address postal mail within the country.
	 */
	POSTAL_CODE,
	
	/**
	 * indicates a prominent natural feature.
	 */
	NATRAL_FEATURE,
	
	/**
	 * indicates an airport.
	 */
	AIRPORT,
	
	/**
	 * indicates a named park.
	 */
	PARK,
	
	/**
	 * indicates a named point of interest. Typically, these "POI"s are prominent local entities that don't easily fit in another category such as "Empire State Building" or "Statue of Liberty."
	 */
	POINT_OF_INTEREST,
	
	/**
	 * indicates a specific postal box.
	 */
	POST_BOX,
	
	/**
	 * indicates the precise street number.
	 */
	STREET_NUMBER,
	
	/**
	 * indicates the floor of a building address.
	 */
	FLOOR,
	
	/**
	 * indicates the room of a building address.
	 */
	ROOM,
	
	/**
	 * indicates a specific bus station
	 */
	BUS_STATION,
	
	/**
	 * indicates a specific transit station
	 */
	TRANSIT_STATION,
	
	/**
	 * indicates a specific establishment
	 */
	ESTABLISHMENT,
	
	UNIVERSITY,
	
	SCHOOL
}

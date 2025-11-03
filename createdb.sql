
CREATE TABLE public.attractions (
    id bigint NOT NULL,
    name character varying NOT NULL,
    description character varying NOT NULL,
    working_hours character varying NOT NULL,
    price bigint NOT NULL,
    url_ref character varying NOT NULL
);

CREATE TABLE public.medias (
    id bigint NOT NULL,
    type character varying NOT NULL,
        name character varying NOT NULL,

    url_ref character varying NOT NULL
);

CREATE TABLE public.stations (
    name character varying NOT NULL,
    id bigint NOT NULL,
    branch bigint NOT NULL,
    description character varying NOT NULL,
    address character varying NOT NULL
);

CREATE TABLE public.attraction_medias (
    attraction_id bigint NOT NULL,
    media_id bigint NOT NULL
);

CREATE TABLE public.station_attractions (
    station_id bigint NOT NULL,
    attraction_id bigint NOT NULL
);

CREATE TABLE public.station_medias (
    station_id bigint NOT NULL,
    media_id bigint NOT NULL
);

ALTER TABLE ONLY public.attractions
    ADD CONSTRAINT "Attraction_pkey" PRIMARY KEY (id);

ALTER TABLE ONLY public.medias
    ADD CONSTRAINT "Meda_pkey" PRIMARY KEY (id);

ALTER TABLE ONLY public.stations
    ADD CONSTRAINT "Station_pkey" PRIMARY KEY (id);

ALTER TABLE ONLY public.attraction_medias
    ADD CONSTRAINT attraction_medias_pkey PRIMARY KEY (attraction_id, media_id);

ALTER TABLE ONLY public.station_attractions
    ADD CONSTRAINT station_attractions_pkey PRIMARY KEY (station_id, attraction_id);

ALTER TABLE ONLY public.station_medias
    ADD CONSTRAINT station_medias_pkey PRIMARY KEY (station_id, media_id);

ALTER TABLE ONLY public.attraction_medias
    ADD CONSTRAINT attraction_id_fk FOREIGN KEY (attraction_id) REFERENCES public.attractions(id) NOT VALID;

ALTER TABLE ONLY public.attraction_medias
    ADD CONSTRAINT media_id_fk FOREIGN KEY (media_id) REFERENCES public.medias(id) NOT VALID;

ALTER TABLE ONLY public.station_attractions
    ADD CONSTRAINT station_attractions_attraction_id_fkey FOREIGN KEY (attraction_id) REFERENCES public.attractions(id) NOT VALID;

ALTER TABLE ONLY public.station_attractions
    ADD CONSTRAINT station_attractions_station_id_fkey FOREIGN KEY (station_id) REFERENCES public.stations(id);

ALTER TABLE ONLY public.station_medias
    ADD CONSTRAINT station_medias_media_id_fkey FOREIGN KEY (media_id) REFERENCES public.medias(id) NOT VALID;

ALTER TABLE ONLY public.station_medias
    ADD CONSTRAINT station_medias_station_id_fkey FOREIGN KEY (station_id) REFERENCES public.stations(id);

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

--;;

CREATE TABLE public.sessions
(
    session_data json not null,
    session_id uuid primary key,
    modified_at timestamp DEFAULT CURRENT_TIMESTAMP
);

--;;

CREATE TABLE public.todos
(
    id uuid NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    label varchar(2048) NOT NULL,
    done boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now()
);
--;;
INSERT INTO public.todos (label) values ('example label from DB');

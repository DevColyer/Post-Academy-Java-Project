-- Create the database
CREATE DATABASE NorseMythology;
USE NorseMythology;

-- User Tables
CREATE TABLE IF NOT EXISTS user
(
    id       BIGINT       NOT NULL
        PRIMARY KEY,
    username VARCHAR(255) NULL,
    password VARCHAR(255) NULL
);

CREATE TABLE IF NOT EXISTS user_roles
(
    user_id BIGINT       NOT NULL,
    role    VARCHAR(255) NULL,
    CONSTRAINT fk_user_roles_on_user
        FOREIGN KEY(user_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS user_seq
(
    next_val BIGINT NULL
);





-- Create the tables
CREATE TABLE IF NOT EXISTS story_source (
                                            source_id INT PRIMARY KEY,
                                            name VARCHAR(255),
                                            discovery_location VARCHAR(255),
                                            discovery_date DATE
);

CREATE TABLE IF NOT EXISTS stories (
                                       story_id INT PRIMARY KEY,
                                       name VARCHAR(255),
                                       source_id INT,
                                       FOREIGN KEY (source_id) REFERENCES story_source(source_id)
);

CREATE TABLE IF NOT EXISTS figures (
                                       figure_id INT PRIMARY KEY,
                                       name VARCHAR(255),
                                       image_link VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS figures_stories (
                                               figure_id INT,
                                               story_id INT,
                                               PRIMARY KEY (figure_id, story_id),
                                               FOREIGN KEY (figure_id) REFERENCES figures(figure_id),
                                               FOREIGN KEY (story_id) REFERENCES stories(story_id)
);

-- Inserting new story sources
INSERT INTO story_source (source_id, name, discovery_location, discovery_date) VALUES
                                                                                   (1, 'Vinland Sagas', 'Iceland', '1200-01-01'),
                                                                                   (2, 'Prose Edda', 'Iceland', '1220-01-01'),
                                                                                   (3, 'Poetic Edda', 'Iceland', '1270-01-01'),
                                                                                   (4, 'Codex Regius', 'Iceland', '1270-01-01'),
                                                                                   (5, 'Hauksbók', 'Iceland', '1334-01-01'),
                                                                                   (6, 'Gylfaginning', 'Iceland', '1220-01-01'),
                                                                                   (7, 'Skáldskaparmál', 'Iceland', '1220-01-01'),
                                                                                   (8, 'Volsung Saga', 'Iceland', '1270-01-01');

-- Inserting new stories
INSERT INTO stories (story_id, name, source_id) VALUES
                                                    (1, 'Saga of Erik the Red', 1),
                                                    (2, 'Saga of the Greenlanders', 1),
                                                    (3, 'Völuspá', 3),
                                                    (4, 'Grímnismál', 3),
                                                    (5, 'Hávamál', 3),
                                                    (6, 'Eiríksmál', 5),
                                                    (7, 'Lokasenna', 6),
                                                    (8, 'Þrymskviða', 7),
                                                    (9, 'Fáfnismál', 3),
                                                    (10, 'Reginsmál', 3),
                                                    (11, 'Volsung Saga', 8);

-- Inserting new figures
INSERT INTO figures (figure_id, name, image_link) VALUES
                                                      (1, 'Thorfinn Karlsefni Thórdarson', 'https://commons.wikimedia.org/wiki/File:Thorfinn_Karlsefni_1918.jpg'),
                                                      (2, 'Erik the Red', 'https://commons.wikimedia.org/wiki/File:Eric_the_Red.png'),
                                                      (3, 'Odin', 'https://commons.wikimedia.org/wiki/File:Odin_by_Johannes_Gehrts.jpg'),
                                                      (4, 'Thor', 'https://commons.wikimedia.org/wiki/File:Thor_by_Johannes_Gehrts.jpg'),
                                                      (5, 'Freya', 'https://commons.wikimedia.org/wiki/File:Freya_by_Johannes_Gehrts.jpg'),
                                                      (6, 'Loki', 'https://commons.wikimedia.org/wiki/File:Loki_by_Mårten_Eskil_Winge.jpg'),
                                                      (7, 'Heimdall', 'https://commons.wikimedia.org/wiki/File:Heimdall_by_Johannes_Gehrts.jpg'),
                                                      (8, 'Baldr', 'https://commons.wikimedia.org/wiki/File:Baldur_by_Johannes_Gehrts.jpg'),
                                                      (9, 'Sigurd', 'https://commons.wikimedia.org/wiki/File:Sigurd_by_Johannes_Gehrts.jpg'),
                                                      (10, 'Fafnir', 'https://commons.wikimedia.org/wiki/File:Fafnir_by_Arthur_Rackham.jpg'),
                                                      (11, 'Brokkr', 'https://commons.wikimedia.org/wiki/File:Brokkr.jpg'),
                                                      (12, 'Sindri', 'https://commons.wikimedia.org/wiki/File:Sindri.jpg'),
                                                      (13, 'Leif Erikson', 'https://commons.wikimedia.org/wiki/File:Leif_Ericson_statue.jpg'),
                                                      (14, 'Bjarni Herjólfsson', 'https://commons.wikimedia.org/wiki/File:Bjarni.jpg'),
                                                      (15, 'Thorvald Eiriksson', 'https://commons.wikimedia.org/wiki/File:Thorvald.jpg'),
                                                      (16, 'Thorstein Eiriksson', 'https://commons.wikimedia.org/wiki/File:Thorstein.jpg'),
                                                      (17, 'Gudrid Thorbjarnardóttir', 'https://commons.wikimedia.org/wiki/File:Gudrid.jpg'),
                                                      (18, 'Freydís Eiríksdóttir', 'https://commons.wikimedia.org/wiki/File:Freydis.jpg');

-- Inserting new relationships between figures and stories
INSERT INTO figures_stories (figure_id, story_id) VALUES
                                                      (1, 1),  -- Thorfinn Karlsefni Thórdarson in Saga of Erik the Red
                                                      (1, 2),  -- Thorfinn Karlsefni Thórdarson in Saga of the Greenlanders
                                                      (2, 1),  -- Erik the Red in Saga of Erik the Red
                                                      (2, 2),  -- Erik the Red in Saga of the Greenlanders
                                                      (3, 3),  -- Odin in Völuspá
                                                      (3, 4),  -- Odin in Grímnismál
                                                      (3, 5),  -- Odin in Hávamál
                                                      (4, 3),  -- Thor in Völuspá
                                                      (4, 4),  -- Thor in Grímnismál
                                                      (5, 3),  -- Freya in Völuspá
                                                      (6, 3),  -- Loki in Völuspá
                                                      (6, 4),  -- Loki in Grímnismál
                                                      (7, 3),  -- Heimdall in Völuspá
                                                      (7, 4),  -- Heimdall in Grímnismál
                                                      (8, 3),  -- Baldr in Völuspá
                                                      (8, 7),  -- Baldr in Lokasenna
                                                      (9, 9),  -- Sigurd in Fáfnismál
                                                      (9, 10), -- Sigurd in Reginsmál
                                                      (10, 9), -- Fafnir in Fáfnismál
                                                      (10, 10),-- Fafnir in Reginsmál
                                                      (11, 10),-- Brokkr in Reginsmál
                                                      (12, 10),-- Sindri in Reginsmál
                                                      (13, 2), -- Leif Erikson in Saga of the Greenlanders
                                                      (14, 2), -- Bjarni Herjólfsson in Saga of the Greenlanders
                                                      (15, 2), -- Thorvald Eiriksson in Saga of the Greenlanders
                                                      (16, 2), -- Thorstein Eiriksson in Saga of the Greenlanders
                                                      (17, 2), -- Gudrid Thorbjarnardóttir in Saga of the Greenlanders
                                                      (18, 2), -- Freydís Eiríksdóttir in Saga of the Greenlanders
                                                      (4, 8),  -- Thor in Þrymskviða
                                                      (6, 7),  -- Loki in Lokasenna
                                                      (9, 11), -- Sigurd in Volsung Saga
                                                      (10, 11);-- Fafnir in Volsung Saga

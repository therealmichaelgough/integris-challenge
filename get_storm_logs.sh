#!/bin/bash
docker exec -it storm_supervisor /bin/bash
tail -f /logs/workers-artifacts/word_counter-*/*/worker.log
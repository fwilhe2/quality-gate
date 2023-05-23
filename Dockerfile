FROM sapmachine:17

COPY . /
RUN ./mvnw --show-version --batch-mode --no-transfer-progress package

ENTRYPOINT ["/entrypoint.sh"]